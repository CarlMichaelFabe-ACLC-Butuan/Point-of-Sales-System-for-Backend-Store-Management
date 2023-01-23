package login;

import database.LoginModel;
import database.data.EmployeeInfo;

public class LoginController {
    private final String department;
    LoginModel loginModel = new LoginModel();

    public LoginController(String department) {
        this.department = department;
    }

    public boolean employeeExists(String username) {
        return this.loginModel.userExists(username, this.department);
    }

    public EmployeeInfo login(String usernameText, String passwordText) {
        return this.loginModel.login(usernameText, passwordText, this.department);
    }
}
