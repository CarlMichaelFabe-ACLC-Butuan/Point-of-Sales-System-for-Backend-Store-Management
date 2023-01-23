import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    public JButton cashierButton = new JButton("CASHIER");
    public JButton inventoryButton = new JButton("INVENTORY");
    private JPanel mainPanel;
    private JPanel bottom;
    private JPanel top;

    public MainUI() {
        super();

        String html = "<html><body><h1 style='text-align: center;'>%1s</h1></body></html>";
        String welcome = "Welcome to Principles of Operating Systems Project";
        String title = "Point of Sales System";
        int fontSize = 30;

        JLabel welcomeLabel = new JLabel(String.format(html, welcome), SwingConstants.CENTER);
        JLabel titleLabel = new JLabel(String.format(html, title), SwingConstants.CENTER);

        this.top.setLayout(new GridLayout(2, 1, 2, 2));
        this.top.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        this.top.add(welcomeLabel);
        this.top.add(titleLabel);

        this.cashierButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        this.inventoryButton.setFont(new Font("Arial", Font.BOLD, fontSize));

        this.bottom.setLayout(new GridLayout(1, 2, 2, 2));
        this.bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.bottom.add(this.cashierButton);
        this.bottom.add(this.inventoryButton);
    }

    public void MainFrame() {
        setContentPane(this.mainPanel);
        setTitle("Point of Sales System");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

    }

}
