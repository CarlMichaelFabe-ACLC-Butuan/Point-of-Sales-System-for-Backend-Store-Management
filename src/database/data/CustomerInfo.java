package database.data;

public class CustomerInfo {
    public final int customerID;
    public final String customerName;
    public Double points;

    public CustomerInfo(int customerID, String customerName, Double points) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.points = points;
    }
}
