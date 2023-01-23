package login;

import abstractions.App;
import database.data.EmployeeInfo;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LoginUI extends App {
    private final List<LoginSuccess> listeners = new ArrayList<>();
    private JPanel mainPanel;
    private JTextField user;
    private JPasswordField password;
    private JButton loginButton;

    public LoginUI(String title, LoginController loginController) {
        this.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeApp");
        this.mainPanel.getActionMap().put("closeApp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeApp();
            }
        });
        this.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
        this.mainPanel.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButtonAction(loginController);
            }
        });
        this.password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
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
