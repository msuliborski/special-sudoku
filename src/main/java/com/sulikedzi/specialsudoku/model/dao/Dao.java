package com.sulikedzi.specialsudoku.model.dao;

import com.sulikedzi.specialsudoku.model.exceptions.DaoException;

public interface Dao<T> {
    T read() throws DaoException;
    void write(T obj) throws DaoException;
}
