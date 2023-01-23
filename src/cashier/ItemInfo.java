package cashier;

import database.data.Product;

public record ItemInfo(Product product, int quantity, int totalPrice) {

    public int productID() {
        return product.productID;
    }

    public String productName() {
        return product.productName;
    }

    public double unitPrice() {
        return product.productPrice;
    }

    public double actualTotalPrice() {
        return (double) this.totalPrice / 100;
    }

    public Object[] getArray() {
        return new Object[] {this, productID(), this.product, unitPrice(), this.quantity, actualTotalPrice()};
    }
}
