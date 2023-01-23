package inventory;

import abstractions.App;
import database.data.EmployeeInfo;
import database.data.Product;
import database.data.UpdateProduct;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class InventoryUI extends App {

    // Data
    private List<Object[]> inventory;
    private Product selectedItem;
    private Product[] selectedItems;
    private int[] currentSelection;

    // Main
    private JPanel mainPanel;
    private JButton resetButton;
    private JButton reloadTableButton;

    // Controls
    private JTabbedPane tabbedPaneControls;
    private JTextField filterSearchField;
    private JTextField filterSearchFieldCD;

    // Update
    private JLabel selectedItemLabel;
    private JLabel selectedItemsLabel;
    private JTextField updateName;
    private JTextField updatePrice;
    private JSpinner updateStock;
    private JButton updateNameButton;
    private JButton updatePriceButton;
    private JButton updateStockButton;

    // Update Multiple
    private JSpinner updateItemsStock;
    private JButton removeStockButton;
    private JButton addStockButton;

    // Add/Remove
    private JTextField addItemName;
    private JTextField addItemPrice;
    private JSpinner addItemStock;
    private JButton addItemButton;
    private JLabel selectedItemsCD;
    private JButton removeItemButton;

    // Right
    private JLabel timeLabel;
    private JLabel dateLabel;
    private JTable databaseTable;

    // Manager Info
    private JLabel managerID;
    private JLabel managerName;
    private JButton logoutButton;

    public InventoryUI(EmployeeInfo employeeInfo) {
        this.employeeInfo = employeeInfo;
        this.managerID.setText(String.valueOf(employeeInfo.employeeID));
        this.managerName.setText(employeeInfo.employeeName);
        this.inventory = getInventory();
        this.currentSelection = new int[0];

        setDate();
        setUpTable();

        this.updateStock.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        this.updateItemsStock.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        this.addItemStock.setModel(new SpinnerNumberModel(0, 0, 1000, 1));

        setUpListeners();

        setContentPane(this.mainPanel);
        setTitle("Inventory Management");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        this.filterSearchField.requestFocus();
    }

    public List<Object[]> getInventory() {
        Product[] products = this.inventoryModel.getAllProducts();
        List<Object[]> inventory = new ArrayList<>();
        for (Product product : products) {
            inventory.add(product.getArray());
        }
        return inventory;
    }

    private void removeFromInventory(Integer[] ids) {

        int items = this.inventoryModel.removeProducts(ids);
        String message =
                String.format("Removed %d %s from the Inventory Database", items, items == 1 ? "item" : "items");
        setFilterAndReloadTable();
        JOptionPane.showMessageDialog(this.mainPanel, message);
    }

    private void updateInventory(UpdateProduct[] products) {
        int items = this.inventoryModel.updateProducts(products);
        String message =
                String.format("Updated %d %s from the Inventory Database", items, items == 1 ? "item" : "items");
        JOptionPane.showMessageDialog(this.mainPanel, message);
    }

    private void setDate() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        Timer timer = new Timer(1000, e -> {
            LocalDateTime dateNow = LocalDateTime.now();
            this.timeLabel.setText(dateNow.format(timeFormatter));
            this.dateLabel.setText(dateNow.format(dateFormatter));
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    private void setUpTable() {
        DefaultTableModel tableModel =
                new DefaultTableModel(
                        null, new String[] {"Product", "Product ID", "Product Name", "Unit Price", "Stock"}) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
        for (Object[] item : this.inventory) {
            tableModel.addRow(item);
        }
        this.databaseTable.setModel(tableModel);
        this.databaseTable.getTableHeader().setReorderingAllowed(false);
        this.databaseTable.getColumnModel().getColumn(2).setMinWidth(400);
        this.databaseTable.getColumnModel().getColumn(0).setMinWidth(0);
        this.databaseTable.getColumnModel().getColumn(0).setMaxWidth(0);

        TableRowSorter<DefaultTableModel> tableModelTableRowSorter = new TableRowSorter<>(tableModel);
        this.databaseTable.setRowSorter(tableModelTableRowSorter);
    }

    private void setUpListeners() {
        this.tabbedPaneControls.addChangeListener(e -> filterOnTab());
        this.filterSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable(filterSearchField);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable(filterSearchField);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable(filterSearchField);
            }
        });
        this.updateNameButton.addActionListener(e -> {
            if (!selectedItemExists()) {
                return;
            }
            String name = this.updateName.getText().trim();
            if (name.trim().isEmpty()) {
                highlightComponent(this.updateName);
                return;
            }
            updateInventory(new UpdateProduct[] {new UpdateProduct(this.selectedItem, name, null, null)});
            reloadTableKeepSelection();
            filterOnTab();
        });
        this.updatePriceButton.addActionListener(e -> {
            if (!selectedItemExists()) {
                return;
            }
            Double price = getValidPrice(this.updatePrice);
            if (price == null) {
                return;
            }
            updateInventory(new UpdateProduct[] {new UpdateProduct(this.selectedItem, null, price, null)});
            reloadTableKeepSelection();
            filterOnTab();
        });
        this.updateStockButton.addActionListener(e -> {
            if (!selectedItemExists()) {
                return;
            }
            int quantity = (int) this.updateStock.getValue();
            updateInventory(new UpdateProduct[] {new UpdateProduct(this.selectedItem, null, null, quantity)});
            reloadTableKeepSelection();
            filterOnTab();
        });

        this.addStockButton.addActionListener(e -> updateStock(false));
        this.removeStockButton.addActionListener(e -> updateStock(true));

        this.filterSearchFieldCD.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable(filterSearchFieldCD);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable(filterSearchFieldCD);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable(filterSearchFieldCD);
            }
        });
        this.addItemButton.addActionListener(e -> {
            String name = this.addItemName.getText().trim();
            if (name.trim().isEmpty()) {
                highlightComponent(this.addItemName);
                return;
            }
            Double price = getValidPrice(this.addItemPrice);
            if (price == null) {
                return;
            }
            int stock = (int) this.addItemStock.getValue();
            this.inventoryModel.addProduct(name, price, stock);
            setFilterAndReloadTable();
            this.databaseTable.scrollRectToVisible(
                    this.databaseTable.getCellRect(this.databaseTable.getRowCount() - 1, 0, true));
            int lastRow = this.databaseTable.getRowCount() - 1;
            this.databaseTable.setRowSelectionInterval(lastRow, lastRow);
        });
        this.removeItemButton.addActionListener(e -> removeItems());

        this.databaseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                return;
            }
            Timer timer = new Timer(100, e1 -> {
                DefaultTableModel tableModel = (DefaultTableModel) this.databaseTable.getModel();

                int[] rawRows = this.databaseTable.getSelectedRows();
                this.currentSelection = rawRows;
                List<Integer> rows = new ArrayList<>();
                for (int row : rawRows) {
                    int modelRow = this.databaseTable.convertRowIndexToModel(row);
                    rows.add(modelRow);
                }
                //                this.currentSelection = rows.stream().mapToInt(i->i).toArray();
                if (rows.size() == 0) {
                    this.selectedItem = null;
                    this.selectedItems = null;
                    this.selectedItemLabel.setText("No Item Selected");
                    this.selectedItemsLabel.setText("No Item Selected");
                    this.selectedItemsCD.setText("No Item Selected");
                    return;
                } else if (rows.size() == 1) {
                    Product selectedProduct = (Product) tableModel.getValueAt(rows.get(0), 0);
                    this.selectedItem = selectedProduct;
                    this.selectedItems = new Product[] {selectedProduct};

                    this.selectedItemLabel.setText(selectedProduct.productName);
                    this.selectedItemsLabel.setText(selectedProduct.productName);
                    this.selectedItemsCD.setText(selectedProduct.productName);
                    return;
                }
                List<Product> productsList = new ArrayList<>();
                for (int row : rows) {
                    Product selectedProduct = (Product) tableModel.getValueAt(row, 0);
                    productsList.add(selectedProduct);
                }
                Product[] products = new Product[productsList.size()];
                productsList.toArray(products);
                this.selectedItem = products[products.length - 1];
                this.selectedItems = products;

                this.selectedItemLabel.setText(this.selectedItem.productName);
                this.selectedItemsLabel.setText(products.length + " items selected");
                this.selectedItemsCD.setText(products.length + " items selected");
            });
            timer.setRepeats(false);
            timer.start();
        });
        this.reloadTableButton.addActionListener(e -> setFilterAndReloadTable());
        this.resetButton.addActionListener(e -> resetApp());
        this.logoutButton.addActionListener(e -> closeApp());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApp();
            }
        });
    }

    private void filterTable(JTextField textField) {
        textField.requestFocus();
        String query = textField.getText();
        TableRowSorter<? extends TableModel> rowSorter =
                (TableRowSorter<? extends TableModel>) this.databaseTable.getRowSorter();
        RowFilter<TableModel, Object> rowFilter;
        try {
            rowFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(query), 1, 2, 3, 4);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        rowSorter.setRowFilter(rowFilter);
        Timer timer = new Timer(100, e -> {
            if (this.currentSelection.length == 0) {
                return;
            }
            this.databaseTable.scrollRectToVisible(
                    this.databaseTable.getCellRect(this.currentSelection[this.currentSelection.length - 1], 0, true));
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void filterOnTab() {
        int currentTab = this.tabbedPaneControls.getSelectedIndex();
        if (currentTab == 0) {
            filterTable(this.filterSearchField);
            return;
        }
        filterTable(this.filterSearchFieldCD);
    }

    private void setFilterAndReloadTable() {
        this.filterSearchField.setText("");
        this.filterSearchFieldCD.setText("");
        this.inventory = getInventory();
        setUpTable();
    }

    private void reloadTableKeepSelection() {
        int[] rows = this.currentSelection;
        this.inventory = getInventory();
        setUpTable();
        selectPreviousSelected(rows);
    }

    private void updateStock(boolean negative) {
        if (!selectedItemExists()) {
            return;
        }
        int value = (int) this.updateItemsStock.getValue();
        if (value == 0) {
            highlightComponent(this.updateItemsStock.getEditor().getComponent(0));
            return;
        }
        if (negative) {
            value = -value;
        }
        List<UpdateProduct> updateProductsList = new ArrayList<>();

        StringBuilder message = new StringBuilder("<html><body>");
        for (Product product : this.selectedItems) {
            int newStock = product.productStock + value;
            message.append(product.productName)
                    .append("&nbsp;&nbsp;&nbsp; >>> &nbsp;&nbsp;&nbsp;( ")
                    .append(newStock)
                    .append(" )<br/>");
            updateProductsList.add(new UpdateProduct(product, null, null, newStock));
        }
        message.append("</body>/html>");

        if (!showConfirmDialog(this.mainPanel, message.toString(), "Update Stock")) {
            return;
        }
        UpdateProduct[] updateProducts = new UpdateProduct[updateProductsList.size()];
        updateProductsList.toArray(updateProducts);
        updateInventory(updateProducts);
        reloadTableKeepSelection();
        filterOnTab();
    }

    private void removeItems() {
        if (!selectedItemExists()) {
            return;
        }
        StringBuilder message = new StringBuilder("<html><body>");
        List<Integer> idsList = new ArrayList<>();
        for (Product product : this.selectedItems) {
            idsList.add(product.productID);
            message.append(product.productID)
                    .append("&nbsp;&nbsp;&nbsp; - &nbsp;&nbsp;&nbsp;")
                    .append(product.productName)
                    .append("<br/>");
        }
        message.append("</body>/html>");

        Integer[] ids = new Integer[idsList.size()];
        idsList.toArray(ids);

        if (!showConfirmDialog(this.mainPanel, message.toString(), ids.length == 1 ? "Remove Item" : "Remove Items")) {
            return;
        }
        removeFromInventory(ids);
    }

    private Double getValidPrice(JTextField textField) {
        try {
            String price = textField.getText();
            if (price.trim().isEmpty()) {
                highlightComponent(textField);
                return null;
            }
            return round(Double.parseDouble(price));
        } catch (NumberFormatException ex) {
            highlightComponent(textField);
            showErrorDialog(this.mainPanel, "Price not a valid number!");
            return null;
        }
    }

    private void selectPreviousSelected(int[] rows) {
        if (rows.length == 0) {
            return;
        }
        this.databaseTable.setRowSelectionInterval(rows[0], rows[0]);
        for (int i = 1; i < rows.length; i++) {
            this.databaseTable.addRowSelectionInterval(rows[i], rows[i]);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean selectedItemExists() {
        if (this.selectedItem == null) {
            showErrorDialog(this.mainPanel, "No Selected Item");
            return false;
        }
        return true;
    }

    private double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
