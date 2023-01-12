package database;

import java.sql.SQLException;

public class LoginController {
    LoginModel loginModel = new LoginModel();

    public LoginController() {
        if (this.loginModel.isDatabaseConnected()) {
            System.out.println("Connected to Database");
        } else {
            System.out.println("Not connected to Database");
            System.exit(1);
        }
    }

    public boolean cashierLogin(String username, String password) {
        try {
            return this.loginModel.login(username, password, "cashier");
        } catch (SQLException e) {
            System.out.println("Something Went Wrong!");
            System.exit(1);
        }
        return false;
    }
}
