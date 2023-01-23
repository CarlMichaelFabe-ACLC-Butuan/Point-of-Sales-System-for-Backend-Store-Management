package login;

import database.LoginModel;
import database.data.EmployeeInfo;

import java.sql.SQLException;

public class LoginController {
    private final String department;
    LoginModel loginModel = new LoginModel();

    public LoginController(String department) {
        this.department = department;
    }

    public boolean employeeExists(String username) {
        try {
            return this.loginModel.userExists(username, this.department);
        } catch (SQLException e) {
            return false;
        }
    }

    public EmployeeInfo login(String usernameText, String passwordText) {
        try {
            return this.loginModel.login(usernameText, passwordText, this.department);
        } catch (SQLException e) {
            System.out.println("Something Went Wrong!");
            System.exit(1);
        }
        return null;
    }
}
