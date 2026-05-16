package com.primeestate.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String URL = "jdbc:mysql://"
            + dotenv.get("DB_HOST", "localhost") + ":"
            + dotenv.get("DB_PORT", "3306") + "/"
            + dotenv.get("DB_NAME", "OOP_Real_state")
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER     = dotenv.get("DB_USER", "root");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD", "");

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    public static DBConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
