package com.sulikedzi.specialsudoku.model.dao;

import com.sulikedzi.specialsudoku.model.exceptions.DaoException;
import com.sulikedzi.specialsudoku.model.logs.FileAndConsoleLoggerFactory;
import com.sulikedzi.specialsudoku.model.sudoku.SudokuBoard;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

public class JdbcSudokuBoardDao implements Dao<SudokuBoard>, AutoCloseable {

    private static final Logger logger = FileAndConsoleLoggerFactory.getConfiguredLogger(JdbcSudokuBoardDao.class.getName());

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String DB_URL = "jdbc:sqlite:SudokuBoards.db";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException cnfex) {
            logger.log(SEVERE, DaoException.NO_JDBC_DRIVER, cnfex);
        }
    }
    private static final String CREATE_BOARDS_TABLE = "CREATE TABLE IF NOT EXISTS boards (" +
            "[boardName] VARCHAR(255)," +
            "[creationDate] VARCHAR(255)" +
            ")";
    private static final String CREATE_FIELDS_TABLE = "CREATE TABLE IF NOT EXISTS fields (" +
            "[x] INTEGER," +
            "[y] INTEGER," +
            "[value] INTEGER," +
            "[isDefault] BOOLEAN," +
            "[boardName] INTEGER" +
            ")";

    private static final String DROP_TABLE_BOARDS = "DROP TABLE IF EXISTS boards";
    private static final String DROP_TABLE_FIELDS = "DROP TABLE IF EXISTS fields";
    private static final String READ_ALL_BOARDS = "SELECT * FROM boards";
    private static final String READ_QUERY_FIELD = "SELECT * FROM fields WHERE [boardName]=?";
    private static final String WRITE_QUERY_BOARD = "INSERT INTO boards([boardName], [creationDate]) VALUES(?, strftime('%d/%m/%Y %H:%M:%S', 'now', 'localtime'))";
    private static final String WRITE_QUERY_FIELD = "INSERT INTO fields([x], [y], [value], [isDefault], [boardName]) VALUES(?, ?, ?, ?, ?)";
    private static final String DELETE_QUERY_BOARD = "DELETE FROM boards WHERE [boardName]=?";
    private static final String DELETE_ALL_BOARD = "DELETE FROM boards";
    private static final String DELETE_QUERY_FIELDS = "DELETE FROM fields WHERE [boardName]=?";
    private static final String DELETE_ALL_FIELDS = "DELETE FROM fields";

    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private String boardName;

    public JdbcSudokuBoardDao(final String name) throws DaoException {

        if (name == null) {
            throw new DaoException(DaoException.NULL_NAME);
        }
        this.boardName = name;

        try {
            connection = DriverManager.getConnection(DB_URL);
            statement = connection.createStatement();
            statement.execute(CREATE_BOARDS_TABLE);
            statement.execute(CREATE_FIELDS_TABLE);
        } catch (SQLException sqle) {
            throw new DaoException(DaoException.SQL_ERROR, sqle);
        }
    }

    @Override
    public SudokuBoard read() throws DaoException {
        try {
            logger.log(Level.INFO, "Read sudoku '" + boardName + "' from database.");
            SudokuBoard sudokuBoard = new SudokuBoard();
            preparedStatement = connection.prepareStatement(READ_QUERY_FIELD);
            preparedStatement.setString(1, boardName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int x = resultSet.getInt(1);
                int y = resultSet.getInt(2);
                sudokuBoard.setFieldValue(x, y, resultSet.getInt(3));
                sudokuBoard.getField(x, y).setDefault(resultSet.getBoolean(4));
            }
            return sudokuBoard;
        } catch (SQLException sqle) {
            throw new DaoException(DaoException.SQL_ERROR, sqle);
        }
    }

    @Override
    public void write(final SudokuBoard sudokuBoard) throws DaoException {
        logger.log(Level.INFO, "Write sudoku '" + boardName + "' to database.");

        if (sudokuBoard == null) {
            throw new DaoException(DaoException.NULL_BOARD);
        }
        try {
            delete();

            preparedStatement = connection.prepareStatement(WRITE_QUERY_BOARD);
            preparedStatement.setString(1, boardName);
            preparedStatement.execute();
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    preparedStatement = connection.prepareStatement(WRITE_QUERY_FIELD);
                    preparedStatement.setInt(1, x);
                    preparedStatement.setInt(2, y);
                    preparedStatement.setInt(3, sudokuBoard.getFieldValue(x, y));
                    preparedStatement.setBoolean(4, sudokuBoard.getField(x, y).isDefault());
                    preparedStatement.setString(5, boardName);
                    preparedStatement.execute();
                }
            }
        } catch (SQLException sqle) {
            throw new DaoException(DaoException.SQL_ERROR, sqle);
        }
    }

    void resetTables() throws DaoException {
        try {
            statement.execute(DROP_TABLE_FIELDS);
            statement.execute(DROP_TABLE_BOARDS);
            statement.execute(CREATE_BOARDS_TABLE);
            statement.execute(CREATE_FIELDS_TABLE);
            statement.execute(DELETE_ALL_BOARD);
            statement.execute(DELETE_ALL_FIELDS);
        } catch (SQLException sqle) {
            throw new DaoException(DaoException.SQL_ERROR, sqle);
        }
    }

    public void delete() throws DaoException {
        logger.log(Level.INFO, "Delete sudoku '" + boardName + "' from database.");
        try {
            preparedStatement = connection.prepareStatement(DELETE_QUERY_BOARD);
            preparedStatement.setString(1, boardName);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(DELETE_QUERY_FIELDS);
            preparedStatement.setString(1, boardName);
            preparedStatement.executeUpdate();
        } catch (SQLException sqle) {
            throw new DaoException(DaoException.SQL_ERROR, sqle);
        }
    }

    public List<String[]> getAllBoardsAsStrings() throws DaoException {
        logger.log(Level.INFO, "Fetching all sudoku from database.");
        try {
            List<String[]> list = new ArrayList<>();
            preparedStatement = connection.prepareStatement(READ_ALL_BOARDS);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String[] array = new String[2];
                array[0] = resultSet.getString(1);
                array[1] = resultSet.getString(2);
                list.add(array);
            }
            return Collections.unmodifiableList(list);
        } catch (SQLException sqle) {
            throw new DaoException(DaoException.SQL_ERROR, sqle);
        }
    }

    @Override
    public void close() {
    }
}

