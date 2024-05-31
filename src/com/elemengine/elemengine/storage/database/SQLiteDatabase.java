package com.elemengine.elemengine.storage.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.elemengine.elemengine.Elemengine;

public class SQLiteDatabase implements Database {

    private File file;

    public SQLiteDatabase(File file) {
        this.file = file;

    }

    @Override
    public Connection connect() {
        if (!this.file.getParentFile().exists()) {
            this.file.mkdirs();
        }

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + this.file.getAbsolutePath());
            Elemengine.plugin().getLogger().info("Database connected!");

            return connection;
        } catch (final ClassNotFoundException e) {
            Elemengine.plugin().getLogger().warning("JDBC driver not found!");
            return null;
        } catch (final SQLException e) {
            Elemengine.plugin().getLogger().warning("SQLite exception during connection.");
            return null;
        }
    }

    @Override
    public String createTable(String name, String[] fields, Class<?>[] types, String[] primaryKey) {
        if (fields.length != types.length) {
            throw new RuntimeException("Arrays 'fields' and 'types' for createTable must be the same length.");
        }
        
        int length = fields.length;
        
        StringBuilder str = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        
        str.append("(");
        
        for (int i = 0; i < length; ++i) {
            str.append(fields[i] + " ");
            
            if (types[i] == Double.TYPE || types[i] == Float.TYPE) {
                str.append("NUMBER");
            } else if (types[i] == Integer.TYPE || types[i] == Long.TYPE || types[i] == Short.TYPE) {
                str.append("INTEGER");
            } else if (types[i] == String.class) {
                str.append("TEXT");
            }
            
            if (i < length - 1) {
                str.append(", ");
            }
        }
        
        if (primaryKey.length > 0) {
            str.append(", PRIMARY KEY (");
            
            for (int i = 0; i < primaryKey.length; ++i) {
                str.append(primaryKey[i]);
                if (i < primaryKey.length - 1) {
                    str.append(", ");
                }
            }
            
            str.append(")");
        }
        
        str.append(")");
        
        return str.toString();
    }

    @Override
    public String selectFrom(String table, String[] fields) {
        return null;
    }

    @Override
    public String insertInto(String table, String[] fields, Object[] values) {
        return null;
    }

    @Override
    public String insertFrom(String table, String[] fields, String source, String[] srcFields) {
        return null;
    }

    @Override
    public String deleteFrom(String table) {
        return null;
    }

    @Override
    public String updateTable(String name, String[] fields, Object[] values) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
