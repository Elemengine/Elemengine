package com.elemengine.elemengine.storage.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DBConnection {

    private final Database db;
    private final Executor queryExecutor = cmd -> new Thread(cmd).start();

    private Connection connection;

    public DBConnection(Database db) {
        this.db = db;
    }

    private void open() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = db.connect();
        }
    }
    
    public boolean exists(String query) {
        try {
            this.open();
            try (Statement stmt = connection.createStatement()) {
                return stmt.executeQuery(query).next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public void read(String query, ResultConsumer action) {
        try {
            this.open();

            try (Statement stmt = connection.createStatement()) {
                action.accept(stmt.executeQuery(query));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> send(String query) {
        return CompletableFuture.runAsync(() -> execute(query), queryExecutor);
    }

    public CompletableFuture<int[]> send(String first, String second, String...batch) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.open();
                try (Statement stmt = connection.createStatement()) {
                    stmt.addBatch(first);
                    stmt.addBatch(second);     

                    for (String query : batch) {
                        stmt.addBatch(query);
                    }

                    return stmt.executeBatch();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }, queryExecutor);
    }
    
    

    private synchronized void execute(String query) {
        try {
            this.open();
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static interface ResultConsumer {
        void accept(ResultSet result) throws SQLException;
    }
}
