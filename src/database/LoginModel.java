package database;

import database.data.EmployeeInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginModel {
    Connection connection = DBConnection.getConnection();

    public boolean userExists(String username, String department) {
        PreparedStatement sqlStatement;
        ResultSet result;

        String sql = "SELECT * FROM " + department.strip() + " WHERE ";
        sql += "username = ?";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setString(1, username);

            result = sqlStatement.executeQuery();
            boolean out = result.next();
            sqlStatement.close();
            return out;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public EmployeeInfo login(String username, String password, String department) {
        PreparedStatement sqlStatement;
        ResultSet result;

        String sql = "SELECT * FROM " + department.strip() + " WHERE ";
        sql += "username = ? and password = ?";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setString(1, username);
            sqlStatement.setString(2, password);

            result = sqlStatement.executeQuery();
            if (result.next()) {
                int employeeID = result.getInt(1);
                String employeeName = result.getString(2);
                sqlStatement.close();
                return new EmployeeInfo(employeeID, employeeName);
            }
            return null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
