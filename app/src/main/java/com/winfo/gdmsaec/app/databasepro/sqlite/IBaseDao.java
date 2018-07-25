package com.winfo.gdmsaec.app.databasepro.sqlite;

public interface IBaseDao<T> {

    /**
     * 插入操作
     * @param entity
     * @return
     */

    long insert(T entity);
}
