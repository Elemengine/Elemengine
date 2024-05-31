package com.elemengine.elemengine.storage.database;

import java.sql.Connection;

public interface Database {

    Connection connect();
    
    String createTable(String name, String[] fields, Class<?>[] types, String[] primaryKey);
    String selectFrom(String table, String[] fields);
    String insertInto(String table, String[] fields, Object[] values);
    String insertFrom(String table, String[] fields, String source, String[] srcFields);
    String deleteFrom(String table);
    String updateTable(String name, String[] fields, Object[] values);
}
