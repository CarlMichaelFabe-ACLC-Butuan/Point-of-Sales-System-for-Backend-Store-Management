package cashier;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CashierUI extends JFrame {
    private JPanel mainPanel;
    private JPanel itemImage;
    private JTable items;
    private JCheckBox transactionNoCheckBox;
    private JLabel transactionNumber;
    private JComboBox selectItem;
    private JSpinner itemQuantity;
    private JLabel availableStocks;
    private JButton addToCart;
    private JLabel totalPrice;
    private JLabel subTotalPrice;
    private JLabel tax;
    private JComboBox paymentMethod;
    private JLabel cash;
    private JLabel change;
    private JButton resetButton;
    private JButton payButton;
    private JButton logoutButton;
    private JLabel cashierName;
    private JLabel cashierID;

    public CashierUI() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public void MainFrame() {
        setContentPane(mainPanel);
        setTitle("Cashier");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 10, 1);
        itemQuantity = new JSpinner(model);
    }
}
