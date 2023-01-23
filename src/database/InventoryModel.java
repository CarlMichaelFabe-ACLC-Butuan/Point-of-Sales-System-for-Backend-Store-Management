package database;

import database.data.Product;
import database.data.UpdateProduct;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryModel {
    Connection connection = DBConnection.getConnection();

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
            return null;
        }
    }

    public Product getProduct(Integer id) {
        PreparedStatement sqlStatement;
        ResultSet result;

        String sql = "SELECT * FROM products WHERE id = ?";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setInt(1, id);

            result = sqlStatement.executeQuery();

            if (result.next()) {
                int productID = result.getInt(1);
                String productName = result.getString(2);
                double productPrice = result.getDouble(3);
                int productQuantity = result.getInt(4);
                sqlStatement.close();
                return new Product(productID, productName, productPrice, productQuantity);
            }
            return null;
        } catch (SQLException ex) {
            return null;
        }
    }

    public int addProduct(String name, Double price, Integer stock) {
        PreparedStatement sqlStatement;

        String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";

        try {
            sqlStatement = this.connection.prepareStatement(sql);
            sqlStatement.setString(1, name);
            sqlStatement.setDouble(2, price);
            sqlStatement.setInt(3, stock);

            int added = sqlStatement.executeUpdate();
            sqlStatement.close();
            return added;
        } catch (SQLException ex) {
            return 0;
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
}
