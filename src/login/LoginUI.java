package login;

import abstractions.App;
import database.data.EmployeeInfo;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class LoginUI extends App {
    private final List<LoginSuccess> listeners = new ArrayList<>();
    private JPanel mainPanel;
    private JTextField user;
    private JPasswordField password;
    private JButton loginButton;

    public LoginUI(String title, LoginController loginController) {
        this.user.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                setEnterEscape(e, loginController);
            }
        });
        this.password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (setEnterEscape(e, loginController)) {
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                    password.setText("");
                }
            }
        });
        this.loginButton.addActionListener(e -> loginButtonAction(loginController));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApp();
            }
        });

        setContentPane(this.mainPanel);
        setTitle(title);
        setSize(350, 130);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    public void addLoginListener(LoginSuccess loginSuccess) {
        listeners.add(loginSuccess);
    }

    public void getLogin(EmployeeInfo employeeInfo) {
        for (LoginSuccess listener : listeners) {
            listener.loginSuccess(employeeInfo);
        }
    }

    private boolean setEnterEscape(KeyEvent e, LoginController loginController) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            loginButtonAction(loginController);
            return true;
        } else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            dispose();
            return true;
        }
        return false;
    }

    private void loginButtonAction(LoginController loginController) {
        String usernameText = this.user.getText();
        String passwordText = String.valueOf(password.getPassword());
        boolean employeeExists = loginController.employeeExists(usernameText);
        EmployeeInfo employeeInfo = loginController.login(usernameText, passwordText);
        if (usernameText.isEmpty()) {
            highlightComponent(this.user);
        } else if (passwordText.isEmpty()) {
            highlightComponent(this.password);
        } else if (!employeeExists) {
            showErrorDialog(this.mainPanel, "User does not exists!");
        } else if (employeeInfo == null) {
            showErrorDialog(this.mainPanel, "Incorrect Credentials!");
        } else {
            getLogin(employeeInfo);
        }
    }
}
