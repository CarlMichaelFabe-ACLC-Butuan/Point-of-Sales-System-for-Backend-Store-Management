package cashier;

import abstractions.App;
import database.data.CustomerInfo;
import database.data.EmployeeInfo;
import database.data.Product;
import database.data.UpdateProduct;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CashierUI extends App {
    private final Comparator<Product> ascendingProductName = Comparator.comparing(o -> o.productName);

    // Data
    private final EmployeeInfo employeeInfo;
    private CustomerInfo customerInfo = null;
    private Product[] inventory;
    private int transactionNumber;
    private int subTotalInt = 0;
    private int taxInt = 0;
    private int totalInt = 0;
    private Integer cashInt = 0;

    // Main
    private JPanel mainPanel;
    private JButton resetButton;

    // Top Bar
    private JCheckBox transactionNoCheckBox;
    private JLabel transactionNumberLabel;
    private JLabel date;

    // Item Selection
    private JTabbedPane selectItemTabbedPane;
    private JComboBox<Product> selectItem;
    private JSpinner itemQuantity;
    private JLabel availableStock;

    // Manual Item Input
    private JTextField manualItemID;
    private JSpinner manualItemQuantity;
    private JLabel manualAvailableStock;

    // Table Controls
    private JButton removeFromCartButton;
    private JButton addToCartButton;
    private JTable itemsTable;

    // Total
    private JLabel subTotalPrice;
    private JLabel taxLabel;
    private JLabel totalPrice;

    // Payment
    private JComboBox<String> paymentMethod;
    private JLabel change;
    private JTextField cashInput;
    private JButton payButton;

    // Item Info
    private JLabel productNameLabel;
    private JLabel productIdLabel;
    private JLabel unitPriceLabel;
    private JLabel quantityLabel;
    private JLabel totalPriceLabel;

    // Customer Info
    private JLabel customerIDLabel;
    private JLabel customerNameLabel;
    private JLabel customerPointsLabel;
    private JButton randomizeCustomerButton;

    // Cashier Info
    private JLabel cashierName;
    private JLabel cashierID;
    private JButton logoutButton;

    public CashierUI(EmployeeInfo employeeInfo) {
        this.employeeInfo = employeeInfo;
        this.cashierID.setText(String.valueOf(employeeInfo.employeeID));
        this.cashierName.setText(employeeInfo.employeeName);
        this.inventory = getInventory();

        setTransactionNumber();
        setDate();
        setUpTable();
        calculateSubTotal();
        updateChange();
        updatePaymentMethod();

        availableStock.setOpaque(true);
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 0, 1);
        this.itemQuantity.setModel(model);
        this.manualItemQuantity.setModel(model);

        DefaultComboBoxModel<Product> selectItemModel = new DefaultComboBoxModel<>(this.inventory);
        this.selectItem.setModel(selectItemModel);
        this.selectItem.setSelectedIndex(-1);

        DefaultComboBoxModel<String> paymentMethodModel =
                new DefaultComboBoxModel<>(new String[] {"Pay in Cash", "Credit Card", "Shop Points"});
        this.paymentMethod.setModel(paymentMethodModel);

        this.cashInput.setHorizontalAlignment(SwingConstants.RIGHT);

        setUpListeners();

        setContentPane(this.mainPanel);
        setTitle("Cashier");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);
        this.selectItem.requestFocus();
    }

    public Product[] getInventory() {
        Product[] products = this.inventoryModel.getAllProducts();
        Arrays.sort(products, ascendingProductName);
        return products;
    }

    private Product findProduct(Integer productID) {
        List<Product> products = Arrays.stream(inventory)
                .filter(p -> Objects.equals(p.productID, productID))
                .toList();
        try {
            return products.get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private void setTransactionNumber() {
        this.transactionNumber = inventoryModel.getTransaction() + 1;
        System.out.println(transactionNumber);
        this.transactionNumberLabel.setText(String.valueOf(this.transactionNumber));
    }

    private void setDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a MMM dd, yyyy");
        Timer timer = new Timer(1000, e -> {
            LocalDateTime dateNow = LocalDateTime.now();
            this.date.setText(dateNow.format(formatter));
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    private void setUpTable() {
        DefaultTableModel tableModel =
                new DefaultTableModel(
                        null,
                        new String[] {"Item", "Product ID", "Product Name", "Unit Price", "Quantity", "Total Price"}) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        this.itemsTable.setModel(tableModel);
        this.itemsTable.getTableHeader().setReorderingAllowed(false);
        this.itemsTable.getColumnModel().getColumn(0).setMinWidth(0);
        this.itemsTable.getColumnModel().getColumn(0).setMaxWidth(0);
    }

    private void setUpListeners() {
        this.selectItem.addActionListener(e -> updateSelectItem());

        this.selectItemTabbedPane.addChangeListener(e -> {
            int currentTab = this.selectItemTabbedPane.getSelectedIndex();
            if (currentTab == 0) {
                updateSelectItem();
            } else {
                updateManualSelectItem();
            }
        });

        this.itemQuantity.addChangeListener(e -> updateSelectItem());

        this.manualItemID.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateManualSelectItem();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateManualSelectItem();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateManualSelectItem();
            }
        });

        this.manualItemQuantity.addChangeListener(e -> updateManualSelectItem());

        this.addToCartButton.addActionListener(e -> {
            int currentTab = this.selectItemTabbedPane.getSelectedIndex();

            Product product;
            JSpinner spinner;
            JLabel stock;
            if (currentTab == 0) {
                product = (Product) this.selectItem.getSelectedItem();
                spinner = this.itemQuantity;
                stock = this.availableStock;
            } else {
                String idString = this.manualItemID.getText().trim();
                if (idString.trim().isEmpty()) {
                    highlightComponent(manualItemID);
                    return;
                }
                int id;
                try {
                    id = Integer.parseInt(idString);
                } catch (NumberFormatException ex) {
                    id = 0;
                }
                product = findProduct(id);
                spinner = this.manualItemQuantity;
                stock = this.manualAvailableStock;
            }

            if (product == null) {
                showErrorDialog(this.mainPanel, "Item Not Found");
                return;
            }
            if (Integer.parseInt(stock.getText()) == 0) {
                showErrorDialog(this.mainPanel, "Out of Stock!");
                return;
            }
            Integer quantity = (Integer) spinner.getValue();
            if (quantity < 1) {
                highlightComponent(spinner.getEditor().getComponent(0));
                return;
            }
            ItemInfo itemInfo = existsInCart(product, quantity);
            Object[] productRow = itemInfo.getArray();

            DefaultTableModel tableModel = (DefaultTableModel) this.itemsTable.getModel();
            tableModel.addRow(productRow);

            int lastRow = tableModel.getRowCount() - 1;
            this.itemsTable.setRowSelectionInterval(lastRow, lastRow);

            product.productStock -= quantity;
            if (currentTab == 0) {
                updateSelectItem();
            } else {
                updateManualSelectItem();
            }
            updateItemInfo(itemInfo);
        });

        this.removeFromCartButton.addActionListener(e -> {
            DefaultTableModel tableModel = (DefaultTableModel) this.itemsTable.getModel();
            int[] selectedRows = this.itemsTable.getSelectedRows();
            if (selectedRows.length == 0) {
                return;
            }
            List<Integer> rows =
                    new ArrayList<>(Arrays.stream(selectedRows).boxed().toList());
            Collections.reverse(rows);
            for (int row : rows) {
                Product product = (Product) tableModel.getValueAt(row, 2);
                int quantity = (int) tableModel.getValueAt(row, 4);
                product.productStock += quantity;
                tableModel.removeRow(row);
            }
            updateSelectItem();
            updateManualSelectItem();

            if (rows.get(rows.size() - 1) == 0) {
                return;
            }
            int lastRow = tableModel.getRowCount() - 1;
            this.itemsTable.setRowSelectionInterval(lastRow, lastRow);
        });

        this.itemsTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                updateItemInfoFromTable();
            }
        });

        this.itemsTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                updateItemInfoFromTable();
            } else {
                calculateSubTotal();
                updateChange();
            }
        });

        this.paymentMethod.addActionListener(e -> updatePaymentMethod());

        this.cashInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateChange();
            }
        });

        this.payButton.addActionListener(e -> payTransaction());

        this.randomizeCustomerButton.addActionListener(e -> {
            this.customerInfo = this.inventoryModel.getCustomer(0);
            if (this.customerInfo == null) {
                return;
            }

            this.customerIDLabel.setText(String.valueOf(this.customerInfo.customerID));
            this.customerNameLabel.setText(this.customerInfo.customerName);
            this.customerPointsLabel.setText(String.valueOf(this.customerInfo.points));
        });

        this.logoutButton.addActionListener(e -> closeApp());

        this.resetButton.addActionListener(e -> resetApp());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApp();
            }
        });
    }

    private void updateItemInput(Product product, JSpinner quantitySpinner, JLabel stockLabel) {
        int stock = product.productStock;
        int quantity = (int) quantitySpinner.getValue();
        stockLabel.setText(String.valueOf(stock));
        SpinnerNumberModel model = new SpinnerNumberModel(Math.min(quantity, stock), 0, stock, 1);
        quantitySpinner.setModel(model);

        ItemInfo itemInfo = getItemInfo(product, quantity);
        updateItemInfo(itemInfo);
    }

    private void updateSelectItem() {
        Product product = (Product) this.selectItem.getSelectedItem();
        if (product == null) {
            return;
        }
        updateItemInput(product, this.itemQuantity, this.availableStock);
    }

    private void updateManualSelectItem() {
        String idString = this.manualItemID.getText().trim();
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            id = 0;
        }
        Product product = findProduct(id);
        if (product == null) {
            return;
        }
        updateItemInput(product, this.manualItemQuantity, this.manualAvailableStock);
    }

    private void updateItemInfo(ItemInfo itemInfo) {
        this.productNameLabel.setText(String.valueOf(itemInfo.productName()));
        this.productIdLabel.setText(String.valueOf(itemInfo.productID()));
        this.unitPriceLabel.setText(String.valueOf(itemInfo.unitPrice()));
        this.quantityLabel.setText(String.valueOf(itemInfo.quantity()));
        this.totalPriceLabel.setText(String.valueOf(itemInfo.actualTotalPrice()));
    }

    private void updateItemInfoFromTable() {
        if (this.itemsTable.getRowCount() == 0) {
            return;
        }
        ItemInfo itemInfo = (ItemInfo) this.itemsTable.getValueAt(this.itemsTable.getSelectedRow(), 0);
        updateItemInfo(itemInfo);
    }

    private ItemInfo existsInCart(Product product, Integer quantity) {
        DefaultTableModel tableModel = (DefaultTableModel) this.itemsTable.getModel();

        List<Integer> productIDs = new ArrayList<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            int productID = (int) tableModel.getValueAt(row, 1);
            productIDs.add(productID);
        }
        int productIndex = productIDs.indexOf(product.productID);
        if (productIndex == -1) {
            return getItemInfo(product, quantity);
        }
        int previousQuantity = (int) tableModel.getValueAt(productIndex, 4);
        tableModel.removeRow(productIndex);
        return getItemInfo(product, quantity + previousQuantity);
    }

    private void calculateSubTotal() {
        DefaultTableModel tableModel = (DefaultTableModel) this.itemsTable.getModel();

        List<Integer> expenses = new ArrayList<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            ItemInfo itemInfo = (ItemInfo) tableModel.getValueAt(row, 0);
            expenses.add(itemInfo.totalPrice());
        }

        int sum = expenses.stream().mapToInt(i -> i).sum();
        int tax = (int) (sum * (5.0 / 100.0));
        int total = sum + tax;

        this.subTotalInt = sum;
        this.taxInt = tax;
        this.totalInt = total;

        this.subTotalPrice.setText(String.valueOf(String.format("%.2f", this.subTotalInt / 100d)));
        this.taxLabel.setText("(5%) " + (tax / 100d));
        this.totalPrice.setText(String.valueOf(String.format("%.2f", this.totalInt / 100d)));
    }

    private ItemInfo getItemInfo(Product product, Integer quantity) {
        int unitPrice = (int) (product.productPrice * 100);
        int totalPrice = unitPrice * quantity;
        return new ItemInfo(product, quantity, totalPrice);
    }

    private void updatePaymentMethod() {
        int paymentMethod = this.paymentMethod.getSelectedIndex();
        if (paymentMethod == 0) {
            this.cashInput.setText("");
            this.cashInput.setEditable(true);
        } else if (paymentMethod == 1) {
            String total = this.totalPrice.getText();
            this.cashInput.setText(total);
            this.cashInput.setEditable(false);
        } else if (paymentMethod == 2) {
            // TODO: create customer credit
            System.out.println("To Be Implemented");
            this.cashInput.setEditable(true);
        }
    }

    private Double parseDouble(String num) {
        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void getCashInput() {
        Double cash = parseDouble(this.cashInput.getText());
        if (cash == null) {
            this.cashInt = null;
            return;
        }
        this.cashInt = (int) (cash * 100);
    }

    private void updateChange() {
        getCashInput();
        if (this.cashInt == null) {
            this.change.setText("0");
            return;
        }
        if (this.cashInt < this.totalInt) {
            this.change.setText("Not enough cash!");
            return;
        }
        int changeInt = this.cashInt - this.totalInt;
        double change = changeInt / 100d;
        this.change.setText(String.format("%.2f", change));
    }

    private void payTransaction() {
        if (this.cashInput.getText().trim().isEmpty()) {
            highlightComponent(this.cashInput);
            return;
        }
        getCashInput();
        if (this.cashInt == null) {
            showErrorDialog(this.mainPanel, "Cash Input not a valid number!");
            return;
        }
        if (this.cashInt < this.totalInt) {
            showErrorDialog(this.mainPanel, "Not enough cash!");
            return;
        }
        DefaultTableModel tableModel = (DefaultTableModel) this.itemsTable.getModel();
        if (tableModel.getRowCount() == 0) {
            showErrorDialog(this.mainPanel, "No items in cart!");
            return;
        }

        Double subTotal = parseDouble(String.format("%.2f", this.subTotalInt / 100d));
        Double tax = parseDouble(String.format("%.2f", this.taxInt / 100d));
        Double total = parseDouble(String.format("%.2f", this.totalInt / 100d));

        List<ItemInfo> itemInfoList = new ArrayList<>();
        List<UpdateProduct> updateProductsList = new ArrayList<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            ItemInfo item = (ItemInfo) tableModel.getValueAt(row, 0);
            itemInfoList.add(item);
            updateProductsList.add(new UpdateProduct(item.product(), null, null, item.product().productStock));
        }

        ItemInfo[] itemInfoArray = new ItemInfo[itemInfoList.size()];
        itemInfoList.toArray(itemInfoArray);

        UpdateProduct[] updateProducts = new UpdateProduct[updateProductsList.size()];
        updateProductsList.toArray(updateProducts);

        this.inventoryModel.updateProducts(updateProducts);

        this.inventory = getInventory();
        setUpTable();
        updateSelectItem();
        updateManualSelectItem();
        calculateSubTotal();
        updateChange();
        updatePaymentMethod();

        if (!this.transactionNoCheckBox.isSelected()) {
            return;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime dateNow = LocalDateTime.now();
        String time = dateNow.format(timeFormatter);
        String date = dateNow.format(dateFormatter);

        Integer customerID = null;
        if (this.customerInfo != null) {
            customerID = this.customerInfo.customerID;
        }
        String paymentMethod =
                Objects.requireNonNull(this.paymentMethod.getSelectedItem()).toString();

        this.inventoryModel.addTransaction(
                subTotal, tax, total, paymentMethod, time, date, this.employeeInfo.employeeID, customerID);
        this.inventoryModel.addItemsInTransaction(this.transactionNumber, itemInfoArray);

        setTransactionNumber();
    }
}
