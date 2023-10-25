package com.elemengine.elemengine.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DBConnection {

    private Database db;
    private Connection connection;
    private Executor queryExecutor = (cmd) -> new Thread(cmd).start();

    public DBConnection(Database db) {
        this.db = db;
    }

    private void open() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = db.connect();
        }
    }

    public ResultSet read(String query) {
        try {
            open();

            return connection.prepareStatement(query).executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public CompletableFuture<Void> send(String query) {
        new Thread(() -> execute(query)).start();
        
        return CompletableFuture.runAsync(() -> execute(query), queryExecutor);
    }

    private synchronized void execute(String query) {
        try {
            this.open();
            final PreparedStatement stmt = connection.prepareStatement(query);
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
