package com.sulikedzi.specialsudoku.model.dao;

import com.sulikedzi.specialsudoku.model.exceptions.DaoException;
import com.sulikedzi.specialsudoku.model.sudoku.SudokuBoard;

import java.io.*;

public class FileSudokuBoardDao implements Dao<SudokuBoard>, AutoCloseable{

    private String fileName;

    FileSudokuBoardDao(final String name) throws DaoException {
        if (name == null) {
            throw new DaoException(DaoException.NULL_NAME);
        }
        fileName = name;
    }

    @Override
    public SudokuBoard read() throws DaoException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName + ".bin"))) {
            return (SudokuBoard) inputStream.readObject();
        } catch (IOException ioe) {
            throw new DaoException(DaoException.MISSING_FILE, ioe);
        } catch (ClassNotFoundException cnfe) {
            throw new DaoException(DaoException.INVALID_CAST, cnfe);
        }
    }

    @Override
    public void write(final SudokuBoard obj) throws DaoException {
        if (obj == null) {
            throw new DaoException(DaoException.NULL_BOARD);
        }
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName + ".bin"))) {
            outputStream.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void close() {
    }
}
