package database.data;

public class UpdateProduct {
    public final Product product;
    public final Integer ID;
    public final String name;
    public final Double price;
    public final Integer stock;

    public UpdateProduct(Product product, String name, Double price, Integer quantity) {
        this.product = product;
        this.ID = product.productID;
        this.name = name;
        this.price = price;
        this.stock = quantity;
    }

    @Override
    public String toString() {
        return "UpdateProduct{ID="
                + this.ID + ", name='"
                + this.product.productName + '\'' + ", newStock="
                + this.product.productStock + '}';
    }
}
