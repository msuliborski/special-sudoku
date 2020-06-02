package com.sulikedzi.specialsudoku.model.sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuFieldTest {

    @Test
    public void toStringTest() {
    }

    @Test
    public void hashCodeTest() {
    }

    @Test
    public void equalsTest() {
    }

    @Test
    public void compareToTest() {

        SudokuField s1 = new SudokuField();
        s1.setFieldValue(1);
        SudokuField s2 = new SudokuField();
        s2.setFieldValue(2);
        SudokuField s3 = new SudokuField();
        s3.setFieldValue(3);
        SudokuField s1a = new SudokuField();
        s1a.setFieldValue(1);

        assertTrue(s2.compareTo(s1) < 0);
        assertTrue(s2.compareTo(s3) > 0);
        assertEquals(s1.compareTo(s1a), 0);
    }

    @Test
    public void cloneTest() {
        try {
            SudokuField s1 = new SudokuField();
            SudokuField s2 = (SudokuField) s1.clone();
            assertEquals(s1, s2);
            assertNotSame(s1, s2);
            assertEquals(s1.getFieldValue(), s2.getFieldValue());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}