package com.winfo.gdmsaec.app.databasepro.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winfo.gdmsaec.app.databasepro.annotations.DbField;
import com.winfo.gdmsaec.app.databasepro.annotations.DbTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BaseDao<T> implements IBaseDao<T> {

    //持有一个数据库操作的引用
    private SQLiteDatabase sqLiteDatabase;
    //持有操作数据库所对应的java类型
    private Class<?> entityClass;
    //表名
    private String tableName;

    private boolean isInit = false;
    private Map<String, Field> cacheMap = new HashMap<>();

    public boolean init(SQLiteDatabase sqLiteDatabase, Class<?> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;

        if (isInit == false) {
            //开始完成自动创建表
            //1.得到一个表名
            tableName = entityClass.getAnnotation(DbTable.class).value();
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }

            //开始建表
            String sql = getCreateTableSql();
            sqLiteDatabase.execSQL(sql);

            initCacheMap();

            isInit = true;
        }
        return isInit;
    }

    private void initCacheMap() {
        //1.如何得到数据库表的所有字段名（查一次空表）
        String sql = "select * from " + tableName + " limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        //2.取到所有的成员变量
        Field[] columnFields = entityClass.getDeclaredFields();
        Field resultField = null;
        for (String columnName : columnNames) {
            for (Field field : columnFields) {
                String fieldAnnotationName = field.getAnnotation(DbField.class).value();
                if (columnName.equals(fieldAnnotationName)) {
                    resultField = field;
                    break;
                }
            }
            if (resultField != null) {
                cacheMap.put(columnName, resultField);
            }
        }
    }

    private String getCreateTableSql() {
        StringBuffer sb = new StringBuffer();
        sb.append("create table if not exists ");
        sb.append(tableName + "(");
        //取到调用层传入的对象的所有变量成员
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();
            if (type == String.class) {
                sb.append(field.getAnnotation(DbField.class).value() + " TEXT,");
            } else if (type == Integer.class) {
                sb.append(field.getAnnotation(DbField.class).value() + " INTEGER,");
            } else if (type == Long.class) {
                sb.append(field.getAnnotation(DbField.class).value() + " LONG,");
            } else if (type == Double.class) {
                sb.append(field.getAnnotation(DbField.class).value() + " DOUBLE,");
            } else if (type == byte[].class) {
                sb.append(field.getAnnotation(DbField.class).value() + " BLOB,");
            } else {
                //不支持的类型
                continue;
            }
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");

        return sb.toString();
    }

    @Override
    public long insert(T entity) {
        Map<String, String> map = getValue(entity);
        //把map中的内容存入到contentValues中
        ContentValues contentValues = getContentValues(map);
        long result = sqLiteDatabase.insert(tableName, null, contentValues);
        return result;
    }

    private Map<String, String> getValue(T entity) {
        HashMap<String, String> map = new HashMap<>();
        Iterator<Field> iterator = cacheMap.values().iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            field.setAccessible(true);
            //获取成员变量的值
            try {
                Object obj = field.get(entity);
                String key = field.getAnnotation(DbField.class).value();
                map.put(key, String.valueOf(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }
}
