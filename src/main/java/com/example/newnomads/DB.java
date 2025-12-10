package com.example.newnomads;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static final String URL = Env.get("DB_URL");
    private static final String USER = Env.get("DB_USER");
    private static final String PASS = Env.get("DB_PASS");

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
