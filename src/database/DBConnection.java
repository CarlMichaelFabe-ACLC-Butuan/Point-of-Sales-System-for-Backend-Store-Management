package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String SQ_CONN = "jdbc:sqlite:database\\pos.sqlite";

    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            return DriverManager.getConnection(SQ_CONN);
        } catch (SQLException ex) {
            System.exit(1);
        }
        return null;
    }

}
