package model;

import javafx.beans.property.*;

public class LossReportDetail {
    private IntegerProperty reportID;
    private IntegerProperty productID;
    private IntegerProperty lostQuantity;
    private StringProperty reason;

    public LossReportDetail() {
        this.reportID = new SimpleIntegerProperty();
        this.productID = new SimpleIntegerProperty();
        this.lostQuantity = new SimpleIntegerProperty();
        this.reason = new SimpleStringProperty();
    }

    public int getReportID() { return reportID.get(); }
    public void setReportID(int id) { this.reportID.set(id); }
    public IntegerProperty reportIDProperty() { return reportID; }

    public int getProductID() { return productID.get(); }
    public void setProductID(int id) { this.productID.set(id); }
    public IntegerProperty productIDProperty() { return productID; }

    public int getLostQuantity() { return lostQuantity.get(); }
    public void setLostQuantity(int qty) { this.lostQuantity.set(qty); }
    public IntegerProperty lostQuantityProperty() { return lostQuantity; }

    public String getReason() { return reason.get(); }
    public void setReason(String r) { this.reason.set(r); }
    public StringProperty reasonProperty() { return reason; }
}
