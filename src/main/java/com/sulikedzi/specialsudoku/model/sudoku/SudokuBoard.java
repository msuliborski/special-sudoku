package com.sulikedzi.specialsudoku.model.sudoku;

import com.sulikedzi.specialsudoku.model.solvers.BacktrackingSudokuSolver;
import com.sulikedzi.specialsudoku.model.solvers.SudokuSolver;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.*;
import java.util.*;

public class SudokuBoard implements Serializable, Cloneable {

    class Pair {
        public int x;
        public int y;

        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private List<List<SudokuField>> board = new ArrayList<>(9);
    private List<Verifier> verifiers = new ArrayList<>(27);
    private int difficulty;

    public SudokuBoard() {
        initialize();
    }

    public SudokuBoard(int diff) {
        if (diff >= 1 && diff <= 3) {
            this.difficulty = diff;
        } else {
            difficulty = 2;
        }

        initialize();
        fillSudoku(difficulty);
    }

    private void initialize() {
        var boxes = new ArrayList<SudokuBox>(9);
        var columns = new ArrayList<SudokuColumn>(9);
        var rows = new ArrayList<SudokuRow>(9);

        for (int i = 0; i < 9; i++) {
            board.add(new ArrayList<>(9));
            columns.add(new SudokuColumn());
            boxes.add(new SudokuBox());
            rows.add(new SudokuRow());
        }
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                board.get(x).add(new SudokuField());

                rows.get(y).addField(board.get(x).get(y));
                columns.get(x).addField(board.get(x).get(y));
            }
        }
        int boxNum = 0;
        for (int x = 0; x < 9; x += 3) {
            for (int y = 0; y < 9; y += 3) {

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        boxes.get(boxNum).addField(board.get(x + i).get(y + j));
                    }
                }
                boxNum++;
            }
        }
        verifiers.addAll(boxes);
        verifiers.addAll(columns);
        verifiers.addAll(rows);

        for (int i = 0; i < 9; i++) {
            board.set(i, Collections.unmodifiableList(board.get(i)));
        }
        board = Collections.unmodifiableList(board);

        verifiers = Collections.unmodifiableList(verifiers);
    }

    public int getFieldValue(int x, int y) {
        return board.get(x).get(y).getFieldValue();
    }

    public void setFieldValue(int x, int y, int value) {
        board.get(x).get(y).setFieldValue(value);
    }

    public SudokuField getField(int x, int y) {
        return board.get(x).get(y);
    }

    public boolean isFieldDefault(int x, int y) {
        return board.get(x).get(y).isDefault();
    }

    public void setFieldDefault(int x, int y, boolean def) {
        board.get(x).get(y).setDefault(def);
    }


    public void fillSudoku(int difficulty) {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        solver.solveSudoku(this);
        switch (difficulty){
            case 1: this.randomNumbersRemove(20); break;
            case 3: this.randomNumbersRemove(60); break;
            default: this.randomNumbersRemove(40);
        }
    }

    private void randomNumbersRemove(int amount) {
        ArrayList<Pair> list = new ArrayList<>();
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                list.add(new Pair(x, y));
            }
        }

        Random rand = new Random();

        for (int i = 0; i < amount; i++) {
            int index = rand.nextInt(list.size() - 1);

            int x = list.get(index).x;
            int y = list.get(index).y;

            board.get(x).get(y).setFieldValue(0);
            board.get(x).get(y).setDefault(false);

            list.remove(index);
        }
    }


    public boolean verify() {
        for (Verifier v : verifiers) {
            if (!v.verify()) {
                return false;
            }
        }

        return true;
    }

    public boolean areThereZeros() {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (board.get(x).get(y).getFieldValue() == 0) {
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("line.separator"));
        sb.append("---------------------------------").append(System.getProperty("line.separator"));
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                sb.append(board.get(x).get(y).toString()).append(" ");
                if (y % 3 == 2 && y != 8) {
                    sb.append("| ");
                }
            }
            sb.append(System.getProperty("line.separator"));
            if (x % 3 == 2 && x != 8) {
                sb.append("- - - - - - - - - - -").append(System.getProperty("line.separator"));
            }
        }
        sb.append("---------------------------------");
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(board)
                .append(verifiers)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object o) {

        if (o == this) {
            return true;
        }

        if (o == null) {
            return false;
        }
        if (!(o instanceof SudokuBoard)) {
            return false;
        }

        SudokuBoard s = (SudokuBoard) o;

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {

                if (!this.board.get(x).get(y).equals(s.board.get(x).get(y))) {

                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Object clone() {
        byte[] object;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
            object = baos.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(object);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            SudokuBoard clone = (SudokuBoard) ois.readObject();
            return clone;
        } catch (IOException | ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return null;
        }
    }
}
