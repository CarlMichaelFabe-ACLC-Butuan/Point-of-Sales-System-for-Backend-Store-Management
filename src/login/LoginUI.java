package login;

import database.data.EmployeeInfo;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class LoginUI extends JFrame {
    private final List<LoginSuccess> listeners = new ArrayList<>();
    private JPanel mainPanel;
    private JTextField user;
    private JPasswordField password;
    private JButton loginButton;

    public LoginUI(String title, LoginController loginController) {
        this.loginButton.addActionListener(e -> loginButtonAction(loginController));
        this.password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    loginButtonAction(loginController);
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                        && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                    password.setText("");
                }
            }
        });

        setContentPane(this.mainPanel);
        setTitle(title);
        setSize(350, 130);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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

    private void loginButtonAction(LoginController loginController) {
        String usernameText = this.user.getText();
        String passwordText = String.valueOf(password.getPassword());
        boolean employeeExists = loginController.employeeExists(usernameText);
        EmployeeInfo employeeInfo = loginController.login(usernameText, passwordText);
        if (usernameText.isEmpty() && passwordText.isEmpty()) {
            JOptionPane.showMessageDialog(this.mainPanel, "Fields are empty!");
        } else if (!employeeExists) {
            JOptionPane.showMessageDialog(this.mainPanel, "User does not exists!");
        } else if (employeeInfo == null) {
            JOptionPane.showMessageDialog(this.mainPanel, "Incorrect Credentials!");
        } else {
            getLogin(employeeInfo);
        }
    }
}
