package cashier;

import database.LoginController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CashierLogin extends JFrame implements ActionListener {
    private JPanel mainPanel;
    private JTextField user;
    private JPasswordField password;
    private JButton loginButton;
    private LoginController loginController = null;
    private CashierUI mainApp = null;

    public CashierLogin(LoginController controller, CashierUI app) {
        loginController = controller;
        mainApp = app;
        loginButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String usernameText = user.getText();
        String passwordText = String.valueOf(password.getPassword());
        if (usernameText.isEmpty() && passwordText.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Fields are empty!");
        } else if (!loginController.cashierLogin(usernameText, passwordText)) {
            JOptionPane.showMessageDialog(mainPanel, "Username or Password Incorrect!");
        } else {
            dispose();
            mainApp.MainFrame();
        }
    }

    public void MainFrame() {
        setContentPane(mainPanel);
        setTitle("Cashier Login");
        setSize(300, 130);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
