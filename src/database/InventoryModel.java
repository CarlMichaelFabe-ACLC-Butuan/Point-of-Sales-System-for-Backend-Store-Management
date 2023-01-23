package database;

import cashier.ItemInfo;
import database.data.CustomerInfo;
import database.data.Product;
import database.data.UpdateProduct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryModel {
    Connection connection = DBConnection.getConnection();

    public CustomerInfo getCustomer(Integer id) {
        PreparedStatement sqlStatement;
        ResultSet result;
        String sql;
        if (id == null) {
            sql = "SELECT * FROM customer ORDER BY RANDOM() LIMIT 1";
        } else {
            sql = "SELECT * FROM customer WHERE id = ?";
        }

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            if (id != null) {
                sqlStatement.setInt(1, id);
            }

            result = sqlStatement.executeQuery();
            if (result.next()) {
                int customerID = result.getInt(1);
                String customerName = result.getString(2);
                double customerPoints = result.getDouble(4);
                sqlStatement.close();
                return new CustomerInfo(customerID, customerName, customerPoints);
            }
            return null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void updateCustomerPoints(Integer id, Double points) {
        PreparedStatement sqlStatement;

        String sql = "UPDATE customer SET points = ? WHERE id = ?";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setDouble(1, points);
            sqlStatement.setInt(2, id);

            sqlStatement.executeUpdate();
            sqlStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Product[] getAllProducts() {
        PreparedStatement sqlStatement;
        ResultSet result;

        String sql = "SELECT * FROM products";

        try {
            sqlStatement = this.connection.prepareStatement(sql);

            result = sqlStatement.executeQuery();

            List<Product> productsList = new ArrayList<>();
            while (result.next()) {
                int productID = result.getInt(1);
                String productName = result.getString(2);
                double productPrice = result.getDouble(3);
                int productQuantity = result.getInt(4);
                Product product = new Product(productID, productName, productPrice, productQuantity);
                productsList.add(product);
            }
            Product[] products = new Product[productsList.size()];
            productsList.toArray(products);
            sqlStatement.close();
            return products;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void addProduct(String name, Double price, Integer stock) {
        PreparedStatement sqlStatement;

        String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setString(1, name);
            sqlStatement.setDouble(2, price);
            sqlStatement.setInt(3, stock);

            sqlStatement.executeUpdate();
            sqlStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int removeProducts(Integer[] ids) {
        if (ids.length == 0) {
            return 0;
        }
        PreparedStatement sqlStatement;

        StringBuilder sql = new StringBuilder("DELETE FROM products WHERE id IN (?");

        sql.append(", ?".repeat(ids.length - 1));
        sql.append(")");

        try {
            sqlStatement = this.connection.prepareStatement(sql.toString());
            for (int index = 0; index < ids.length; index++) {
                sqlStatement.setInt(index + 1, ids[index]);
            }

            int removed = sqlStatement.executeUpdate();
            sqlStatement.close();
            return removed;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int updateProducts(UpdateProduct[] products) {
        if (products.length == 0) {
            return 0;
        }
        PreparedStatement sqlStatement;

        StringBuilder sql = new StringBuilder("INSERT INTO products (id, name, price, stock) VALUES (?, ?, ?, ?)");

        sql.append(", (?, ?, ?, ?)".repeat(products.length - 1));
        sql.append(
                "  ON CONFLICT(id) DO UPDATE SET id=id, name=excluded.name, price=excluded.price, stock=excluded.stock");

        try {
            sqlStatement = this.connection.prepareStatement(sql.toString());
            for (int index = 0; index < products.length; index++) {
                UpdateProduct product = products[index];
                int pIndex = (index + 1) * 4;
                sqlStatement.setInt(pIndex - 3, product.ID);
                if (product.name == null) {
                    sqlStatement.setString(pIndex - 2, product.product.productName);
                } else {
                    sqlStatement.setString(pIndex - 2, product.name);
                }
                sqlStatement.setDouble(
                        pIndex - 1, Objects.requireNonNullElseGet(product.price, () -> product.product.productPrice));
                sqlStatement.setInt(
                        pIndex, Objects.requireNonNullElseGet(product.stock, () -> product.product.productStock));
            }

            int updated = sqlStatement.executeUpdate();
            sqlStatement.close();
            return updated;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public Integer getTransaction() {
        PreparedStatement sqlStatement;
        ResultSet result;

        String sql = "SELECT MAX(id) FROM transactions";

        try {
            sqlStatement = this.connection.prepareStatement(sql);

            result = sqlStatement.executeQuery();

            if (result.next()) {
                int transaction = result.getInt(1);
                sqlStatement.close();
                return transaction;
            }
            return 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void addTransaction(
            Double subTotal,
            Double tax,
            Double total,
            String paymentMethod,
            String time,
            String date,
            Integer cashierID,
            Integer customerID) {
        PreparedStatement sqlStatement;

        String sql =
                "INSERT INTO transactions (sub_total, tax, total, payment_method, time, date, cashier_id, customer_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setDouble(1, subTotal);
            sqlStatement.setDouble(2, tax);
            sqlStatement.setDouble(3, total);
            sqlStatement.setString(4, paymentMethod);
            sqlStatement.setString(5, time);
            sqlStatement.setString(6, date);
            sqlStatement.setInt(7, cashierID);
            if (customerID == null) {
                sqlStatement.setNull(8, Types.NULL);
            } else {
                sqlStatement.setInt(8, customerID);
            }

            sqlStatement.executeUpdate();
            sqlStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addItemsInTransaction(Integer transactionID, ItemInfo[] items) {
        if (items.length == 0) {
            return;
        }
        PreparedStatement sqlStatement;

        StringBuilder sql = new StringBuilder(
                "INSERT INTO items_in_transaction (transaction_id, product_id, quantity) VALUES (?, ?, ?)");

        sql.append(", (?, ?, ?)".repeat(items.length - 1));

        try {
            sqlStatement = this.connection.prepareStatement(sql.toString());
            for (int index = 0; index < items.length; index++) {
                int pIndex = (index + 1) * 3;
                sqlStatement.setInt(pIndex - 2, transactionID);
                sqlStatement.setInt(pIndex - 1, items[index].product().productID);
                sqlStatement.setInt(pIndex, items[index].quantity());
            }

            sqlStatement.executeUpdate();
            sqlStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
