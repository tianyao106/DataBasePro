package com.winfo.gdmsaec.app.databasepro.sqlite;

import android.database.sqlite.SQLiteDatabase;

public class BaseDaoFactory {

    private static final BaseDaoFactory ourInstance = new BaseDaoFactory();

    public static BaseDaoFactory getInstance() {
        return ourInstance;
    }

    private SQLiteDatabase sqLiteDatabase;
    private String path;

    private BaseDaoFactory() {
        path = "data/data/com.winfo.gdmsaec.app.databasepro/xiaolu.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(path, null);
    }

    public <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseDao;
    }
}
