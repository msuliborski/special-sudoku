package com.sulikedzi.specialsudoku.model.solvers;

import com.sulikedzi.specialsudoku.model.sudoku.SudokuBoard;

import java.util.ArrayList;
import java.util.Random;

public class BacktrackingSudokuSolver implements SudokuSolver {

    @Override
    public boolean solveSudoku(final SudokuBoard board) {

        if (!board.areThereZeros()) {
            return true;
        }

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {

                if (board.getFieldValue(x, y) == 0) {
                    var list = new ArrayList<Integer>(10);
                    for (int i = 1; i < 10; i++) {
                        list.add(i);
                    }

                    Random rand = new Random();
                    while (list.size() > 0) {
                        int index = rand.nextInt(list.size());
                        int i = list.get(index);
                        list.remove(index);
                        board.setFieldValue(x, y, i);
                        if (board.verify()) {
                            if (!solveSudoku(board)) {
                                board.setFieldValue(x, y, 0);
                            } else {
                                return true;
                            }
                        }
                    }
                    board.setFieldValue(x, y, 0);
                    return false;
                }
            }
        }
        return false;
    }
}