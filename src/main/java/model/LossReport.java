package model;

import javafx.beans.property.*;
import java.sql.Timestamp;

public class LossReport {
    private IntegerProperty reportID;
    private IntegerProperty employeeID;
    private ObjectProperty<Timestamp> reportDate;

    private IntegerProperty entryDetailID;
    private IntegerProperty quantity;
    private StringProperty reason;

    public LossReport() {
        this.reportID = new SimpleIntegerProperty();
        this.employeeID = new SimpleIntegerProperty();
        this.reportDate = new SimpleObjectProperty<>();

        this.entryDetailID = new SimpleIntegerProperty();
        this.quantity = new SimpleIntegerProperty();
        this.reason = new SimpleStringProperty();
    }

    public int getReportID() { return reportID.get(); }
    public void setReportID(int id) { this.reportID.set(id); }
    public IntegerProperty reportIDProperty() { return reportID; }

    public int getEmployeeID() { return employeeID.get(); }
    public void setEmployeeID(int id) { this.employeeID.set(id); }
    public IntegerProperty employeeIDProperty() { return employeeID; }

    public Timestamp getReportDate() { return reportDate.get(); }
    public void setReportDate(Timestamp date) { this.reportDate.set(date); }
    public ObjectProperty<Timestamp> reportDateProperty() { return reportDate; }


    public int getEntryDetailID() { return entryDetailID.get(); }
    public void setEntryDetailID(int entryDetailID) { this.entryDetailID.set(entryDetailID); }
    public IntegerProperty entryDetailIDProperty() { return entryDetailID; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public IntegerProperty quantityProperty() { return quantity; }

    public String getReason() { return reason.get(); }
    public void setReason(String reason) { this.reason.set(reason); }
    public StringProperty reasonProperty() { return reason; }
}
