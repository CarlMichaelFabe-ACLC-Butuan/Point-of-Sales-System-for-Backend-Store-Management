package database.data;

public class Product {
    public final int productID;
    public final String productName;
    public final double productPrice;
    public int productStock;

    public Product(int id, String name, double price, int quantity) {
        this.productID = id;
        this.productName = name;
        this.productPrice = price;
        this.productStock = quantity;
    }

    public Object[] getArray() {
        return new Object[] {this, this.productID, this.productName, this.productPrice, this.productStock};
    }

    public String toString() {
        return this.productName;
    }
}
