package com.elemengine.elemengine.storage.database.sqlite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.storage.database.DataKey;
import com.elemengine.elemengine.storage.database.DataName;
import com.elemengine.elemengine.storage.database.DataSchema;
import com.elemengine.elemengine.storage.database.Database;
import com.elemengine.elemengine.util.data.Box;
import com.elemengine.elemengine.util.reflect.Fields;

public class SQLiteDatabase extends Database {

    private Connection connection = null;
    private final File file;
    
    public SQLiteDatabase() {
        this.file = new File(Elemengine.getFolder(), "storage.db");
    }
    
    @Override
    public void setup() {
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

            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.file.getAbsolutePath());
            Elemengine.plugin().getLogger().info("Database connected!");
        } catch (final ClassNotFoundException e) {
            Elemengine.plugin().getLogger().warning("JDBC driver not found!");
        } catch (final SQLException e) {
            Elemengine.plugin().getLogger().warning("SQLite exception during connection.");
        }
    }
    
    private synchronized void read(CharSequence query, ResultConsumer consumer) {
        Elemengine.logger().info(query.toString());
        try {
            if (connection == null || connection.isClosed()) {
                this.setup();
            }
            
            try (Statement stmt = connection.createStatement()) {
                consumer.accept(stmt.executeQuery(query.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean send(CharSequence query) {
        Elemengine.logger().info(query.toString());
        try {
            if (connection == null || connection.isClosed()) {
                this.setup();
            }
            
            try (Statement stmt = connection.createStatement()) {
                if (!stmt.execute(query.toString())) {
                    return stmt.getUpdateCount() > -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean create(Class<? extends DataSchema> type) {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        query.append(type.getSimpleName());
        query.append(" (");
        
        StringBuilder primaryKey = new StringBuilder();
        Field[] fields = type.getDeclaredFields();
        
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            if (field.isAnnotationPresent(DataName.class)) {
                query.append(field.getAnnotation(DataName.class).value());
            } else {
                query.append(field.getName());
            }
            
            query.append(" ");
            
            if (field.isAnnotationPresent(SQLiteType.class)) {
                query.append(field.getAnnotation(SQLiteType.class).value());
            } else {
                query.append(parseType(field));
            }
            
            if (i < fields.length - 1) {
                query.append(", ");
            }
            
            if (field.isAnnotationPresent(DataKey.class)) {
                if (!primaryKey.isEmpty()) {
                    primaryKey.append(", ");
                }
                
                primaryKey.append(field.getName());
            }
        }
        
        if (!primaryKey.isEmpty()) {
            query.append(", PRIMARY KEY (");
            query.append(primaryKey);
            query.append(")");
        }
        
        query.append(")");
        
        return this.send(query);
    }

    @Override
    public boolean set(DataSchema schema) {
        StringBuilder query = new StringBuilder("INSERT OR REPLACE INTO ");
        query.append(schema.getClass().getSimpleName());
        query.append(" VALUES (");
        
        Field[] fields = schema.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            query.append(translateObj(Fields.get(schema, fields[i]).get()));
            
            if (i < fields.length - 1) {
                query.append(", ");
            }
        }
        
        query.append(")");
        
        return this.send(query);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataSchema> Optional<T> get(T schema) {
        Class<T> type = (Class<T>) schema.getClass();
        
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(type.getSimpleName());
        
        StringBuilder where = new StringBuilder();
        for (Field field : type.getDeclaredFields()) {
            Object value = Fields.get(schema, field).orElse(null);
            
            if (value != null) {
                if (!where.isEmpty()) {
                    where.append(" AND ");
                }
                
                where.append(field.getName());
                where.append(" = ");
                where.append(translateObj(value));
            }
        }
        
        if (!where.isEmpty()) {
            query.append(" WHERE ");
            query.append(where);
        }
        
        Box<T> box = Box.empty();
        this.read(query, rs -> {
            try {
                if (!rs.next()) {
                    Elemengine.logger().info("NOT FOUND");
                    return;
                }
                
                T obj = type.getConstructor().newInstance();
                for (Field field : type.getDeclaredFields()) {
                    Fields.set(obj, field, parseData(field, rs));
                }
                
                Elemengine.logger().info("FOUND");
                box.set(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        return box.into(Optional::ofNullable);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataSchema> Deque<T> getAll(T schema) {
        Class<T> type = (Class<T>) schema.getClass();
        
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(type.getSimpleName());
        
        StringBuilder where = new StringBuilder();
        for (Field field : type.getDeclaredFields()) {
            Object value = Fields.get(schema, field).orElse(null);
            
            if (value != null) {
                if (!where.isEmpty()) {
                    where.append(" AND ");
                }
                
                where.append(field.getName());
                where.append(" = ");
                where.append(translateObj(value));
            }
        }
        
        if (!where.isEmpty()) {
            query.append(" WHERE ");
            query.append(where);
        }
        
        Deque<T> found = new ArrayDeque<>();
        this.read(query, rs -> {
            try {
                while (rs.next()) {
                    T obj = type.getConstructor().newInstance();
                    for (Field field : type.getDeclaredFields()) {
                        Fields.set(obj, field, parseData(field, rs));
                    }
                    
                    found.addLast(obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        return found;
    }

    @Override
    public boolean delete(DataSchema schema) {
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(schema.getClass().getSimpleName());
        
        StringBuilder where = new StringBuilder();
        for (Field field : schema.getClass().getDeclaredFields()) {
            Object value = Fields.get(schema, field).orElse(null);
            
            if (value != null) {
                if (!where.isEmpty()) {
                    where.append(" AND ");
                }
                
                where.append(field.getName());
                where.append(" = ");
                where.append(translateObj(value));
            }
        }
        
        if (!where.isEmpty()) {
            query.append(" WHERE ");
            query.append(where);
        }
        
        return this.send(query);
    }
    
    private Object parseData(Field field, ResultSet rs) {
        Class<?> type = field.getType();
        String column = field.getName();
        
        try {
            if (type == Byte.class || type == Byte.TYPE) {
                return rs.getByte(column);
            } else if (type == Short.class || type == Short.TYPE) {
                return rs.getShort(column);
            } else if (type == Integer.class || type == Integer.TYPE) {
                return rs.getInt(column);
            } else if (type == Long.class || type == Long.TYPE) {
                return rs.getLong(column);
            } else if (type == Float.class || type == Float.TYPE) {
                return rs.getFloat(column);
            } else if (type == Double.class || type == Double.TYPE) {
                return rs.getDouble(column);
            } else if (type == Boolean.class || type == Boolean.TYPE) {
                return rs.getBoolean(column);
            } else if (type == String.class) {
                return rs.getString(column);
            } else {
                return rs.getObject(column);
            }
        } catch (Exception e) {}
        
        return null;
    }
    
    private String parseType(Field field) {
        Class<?> type = field.getType();
        
        if (type == Double.TYPE || type == Float.TYPE) {
            return "REAL";
        } else if (type == Byte.TYPE || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE) {
            return "INTEGER";
        } else if (type == Boolean.TYPE) {
            return "NUMERIC";
        }
        
        return "TEXT";
    }
    
    private String translateObj(Object obj) {
        if (obj == null) {
            return "NULL";
        } else if (obj.getClass() == String.class) {
            return "'" + obj.toString() + "'";
        } else if (obj.getClass() == Boolean.TYPE) {
            return ((Boolean) obj).booleanValue() ? "1" : "0";
        }
        
        return obj.toString();
    }
    
    public static interface ResultConsumer {
        void accept(ResultSet result) throws SQLException;
    }
}
