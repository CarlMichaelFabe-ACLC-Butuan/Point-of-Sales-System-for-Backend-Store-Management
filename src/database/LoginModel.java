package database;

import database.data.EmployeeInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginModel {
    Connection connection = DBConnection.getConnection();

    public boolean userExists(String username, String department) throws SQLException {
        PreparedStatement sqlStatement = null;
        ResultSet result = null;

        String sql = "SELECT * FROM " + department.strip() + " WHERE ";
        sql += "username = ?";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setString(1, username);

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

    public EmployeeInfo login(String username, String password, String department) throws SQLException {
        PreparedStatement sqlStatement = null;
        ResultSet result = null;

        String sql = "SELECT * FROM " + department.strip() + " WHERE ";
        sql += "username = ? and password = ?";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setString(1, username);
            sqlStatement.setString(2, password);

            result = sqlStatement.executeQuery();
            if (result.next()) {
                return new EmployeeInfo(result.getInt(1), result.getString(2));
            }
            return null;
        } catch (SQLException ex) {
            return null;
        } finally {
            assert sqlStatement != null;
            assert result != null;
            sqlStatement.close();
            result.close();
        }
    }
}
