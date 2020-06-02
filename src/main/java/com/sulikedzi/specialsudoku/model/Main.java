package com.sulikedzi.specialsudoku.model;

import com.sulikedzi.specialsudoku.model.solvers.SudokuSolver;
import com.sulikedzi.specialsudoku.model.dao.SudokuBoardDaoFactory;
import com.sulikedzi.specialsudoku.model.logs.FileAndConsoleLoggerFactory;
import com.sulikedzi.specialsudoku.model.solvers.BacktrackingSudokuSolver;
import com.sulikedzi.specialsudoku.model.sudoku.SudokuBoard;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

    private static final Logger logger = FileAndConsoleLoggerFactory.getConfiguredLogger(Main.class.getName());

    public static void main(final String[] args) {

        SudokuBoardDaoFactory factory = new SudokuBoardDaoFactory();

        SudokuBoard sudoku = new SudokuBoard();

        sudoku.fillSudoku(2);


        logger.log(Level.INFO, "Sudoku to solve:");
        logger.log(Level.INFO, sudoku.toString());

        logger.log(Level.INFO, "Now we are solving sudoku...");
        SudokuSolver solver = new BacktrackingSudokuSolver();
        solver.solveSudoku(sudoku);

        logger.log(Level.INFO, "Here is solved sudoku:");
        logger.log(Level.INFO, sudoku.toString());
    }
}
