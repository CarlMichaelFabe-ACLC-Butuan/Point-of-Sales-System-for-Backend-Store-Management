package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginModel {
    Connection connection;

    public LoginModel() {
        try {
            this.connection = DBConnection.getConnection();
        } catch (SQLException ex) {
            System.exit(1);
        }
    }

    public boolean isDatabaseConnected() {
        return this.connection != null;
    }

    public boolean login(String username, String password, String department) throws SQLException {
        PreparedStatement sqlStatement = null;
        ResultSet result = null;

        String sql = "SELECT * FROM " + department.strip() + " WHERE ";
        sql += "username = ? and password = ?";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setString(1, username);
            sqlStatement.setString(2, password);

            result = sqlStatement.executeQuery();

            return result.next();
        } catch (SQLException ex) {
            return false;
        } finally {
            assert sqlStatement != null;
            assert result != null;
            sqlStatement.close();
            result.close();
        }
    }
}
