import abstractions.App;
import cashier.CashierUI;
import database.data.EmployeeInfo;
import inventory.InventoryUI;
import login.LoginController;
import login.LoginUI;

interface CreateUI {
    App createApp(EmployeeInfo employeeInfo);
}

class OpenApp extends App {
    private final LoginUI loginUI;

    public OpenApp(String department, String loginTitle, CreateUI createUI) {
        LoginController loginController = new LoginController(department);
        this.loginUI = new LoginUI(loginTitle, loginController);
        this.loginUI.addLoginListener(employeeInfo -> runApp(employeeInfo, createUI));
        this.loginUI.addCloseListener(() -> {
            this.loginUI.dispose();
            closeApp();
        });
    }

    private void runApp(EmployeeInfo employeeInfo, CreateUI createUI) {
        App app = createUI.createApp(employeeInfo);
        this.loginUI.dispose();

        app.addCloseListener(() -> {
            app.dispose();
            closeApp();
        });

        app.addResetListener(() -> {
            app.dispose();
            runApp(employeeInfo, createUI);
        });
    }
}

public class Main {
    static MainUI mainUI = new MainUI();

    public static void main(String[] args) {
        mainUI.cashierButton.addActionListener(e -> openCashier());
        mainUI.inventoryButton.addActionListener(e -> openInventoryManager());
        mainUI.MainFrame();
    }

    private static void openCashier() {
        OpenApp openApp = new OpenApp("cashier", "Cashier Login", CashierUI::new);
        openApp.addCloseListener(() -> mainUI.setVisible(true));
        mainUI.setVisible(false);
    }

    private static void openInventoryManager() {
        OpenApp openApp = new OpenApp("manager", "Inventory Manager Login", InventoryUI::new);
        openApp.addCloseListener(() -> mainUI.setVisible(true));
        mainUI.setVisible(false);
    }
}
