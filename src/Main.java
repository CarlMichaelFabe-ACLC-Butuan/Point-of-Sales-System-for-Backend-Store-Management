import cashier.CashierUI;
import cashier.CashierLogin;
import database.LoginController;

public class Main {
    public static void main(String[] args) {
        LoginController loginController = new LoginController();

        CashierUI app = new CashierUI();
        CashierLogin login = new CashierLogin(loginController, app);
        login.MainFrame();

    }
}