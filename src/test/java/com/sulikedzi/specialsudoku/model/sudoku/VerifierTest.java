package com.sulikedzi.specialsudoku.model.sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VerifierTest {

    @Test
    public void cloneTest(){
        SudokuRow r1 = new SudokuRow();
        for (int i = 0; i < 9; i++) {
            r1.addField(new SudokuField(i+1));
        }
        SudokuRow r2 = (SudokuRow) r1.clone();

        assertNotSame(r1, r2);
        assertEquals(r1, r2);
    }

}