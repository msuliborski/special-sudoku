package com.sulikedzi.specialsudoku.model.dao;

import com.sulikedzi.specialsudoku.model.solvers.SudokuSolver;
import org.junit.jupiter.api.Test;
import com.sulikedzi.specialsudoku.model.exceptions.DaoException;
import com.sulikedzi.specialsudoku.model.solvers.BacktrackingSudokuSolver;
import com.sulikedzi.specialsudoku.model.sudoku.SudokuBoard;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcSudokuBoardDaoTest {

    @Test
    public void testCreateDaoWithNullName() {
        assertThrows(DaoException.class, () -> new JdbcSudokuBoardDao(null));
    }

    @Test
    public void testWriteNullBoard() {
        try (JdbcSudokuBoardDao dao = new JdbcSudokuBoardDao("sudoku")) {
            assertThrows(DaoException.class, () -> dao.write(null));
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readAndWriteBoardTest() {
        SudokuBoard sudokuBoard1 = new SudokuBoard();
        SudokuSolver solver = new BacktrackingSudokuSolver();
        solver.solveSudoku(sudokuBoard1);

        SudokuBoardDaoFactory sudokuBoardDaoFactory = new SudokuBoardDaoFactory();
        try (JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) sudokuBoardDaoFactory.getDatabaseDao("sudoku")) {
            dao.write(sudokuBoard1);
            SudokuBoard sudokuBoard2 = dao.read();
            assertEquals(sudokuBoard1, sudokuBoard2);
            dao.delete();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void displayAllTest() {
        SudokuBoard sudokuBoard1 = new SudokuBoard();
        SudokuSolver solver = new BacktrackingSudokuSolver();
        solver.solveSudoku(sudokuBoard1);

        SudokuBoardDaoFactory sudokuBoardDaoFactory = new SudokuBoardDaoFactory();
        try (JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) sudokuBoardDaoFactory.getDatabaseDao("sudoku")) {
            dao.write(sudokuBoard1);
            List<String[]> list = dao.getAllBoardsAsStrings();
            assertEquals(1, list.size());
            String[] array = list.get(0);
            assertEquals("sudoku", array[0]);
            dao.delete();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }
}