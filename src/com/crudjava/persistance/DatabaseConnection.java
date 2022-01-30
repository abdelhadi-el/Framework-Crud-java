package com.crudjava.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection(String url, String username, String password, String driver) throws SQLException {
            this.connection = DriverManager.getConnection(url, username, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance(String url, String username, String password, String driver) throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection(url, username, password, driver);
        } 
        return instance;
    }

    public static DatabaseConnection getInstance(){
        return instance;
    }
    
}