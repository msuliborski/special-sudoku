package com.sulikedzi.specialsudoku.model.solvers;

import com.sulikedzi.specialsudoku.model.sudoku.SudokuBoard;

public interface SudokuSolver {
    boolean solveSudoku(SudokuBoard board);
}
