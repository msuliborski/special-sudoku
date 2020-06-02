package com.sulikedzi.specialsudoku.model.sudoku;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Verifier implements Serializable, Cloneable {

    private List<SudokuField> fields = new ArrayList<>();
    private int addCounter = 0;

    public void addField(final SudokuField field) {
        if (addCounter == 9) {
            fields = Collections.unmodifiableList(fields);
        } else {
            fields.add(field);
        }
        addCounter++;
    }

    public boolean verify() {
        for (int i = 0; i < 8; i++) {
            for (int j = i + 1; j < 9; j++) {
                if (fields.get(i).getFieldValue() != 0 && fields.get(j).getFieldValue() != 0) {
                    if (fields.get(i).equals(fields.get(j))) {
                        return false;
                    }
                }
//                else {
//                    return false;
//                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(final Object o) {

        if (o == this) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (!(o instanceof Verifier)) {
            return false;
        }

        Verifier v = (Verifier) o;

        for (int i = 0; i < 9; i++) {
            if (!v.fields.get(i).equals(this.fields.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fields)
                .append(addCounter)
                .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(fields.get(i));
        }
        sb.append(", counter = ").append(addCounter);
        return sb.toString();
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

            return ois.readObject();
        } catch (IOException | ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return null;
        }
    }
}
