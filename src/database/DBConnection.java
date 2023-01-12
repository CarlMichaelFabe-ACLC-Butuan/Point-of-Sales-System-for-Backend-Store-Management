package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String USERNAME = "db-user";
    private static final String PASSWORD = "db-pass";
    private static final String SQCONN = "jdbc:sqlite:database\\pos.sqlite";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(SQCONN);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {

    }
}
