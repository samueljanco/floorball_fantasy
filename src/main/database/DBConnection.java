package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static final String url = "jdbc:mysql://sql8.freemysqlhosting.net/sql8619360";
    public static final String username = "sql8619360";
    public static final String password = "KJlCBFKTtM";

    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
