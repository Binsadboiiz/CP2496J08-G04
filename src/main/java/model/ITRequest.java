package model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class ITRequest {
    private IntegerProperty reqID;
    private StringProperty reqName;
    private ObjectProperty<LocalDate> reqDate;
    private StringProperty reqEmail;
    private StringProperty reqType;
    private StringProperty reqDetails;

    public ITRequest(int reqID, String reqName, LocalDate reqDate, String reqEmail, String reqType, String reqDetails) {
        this.reqID = new SimpleIntegerProperty(reqID);
        this.reqName = new SimpleStringProperty(reqName);
        this.reqDate = new SimpleObjectProperty<>(reqDate);
        this.reqEmail = new SimpleStringProperty(reqEmail);
        this.reqType = new SimpleStringProperty(reqType);
        this.reqDetails = new SimpleStringProperty(reqDetails);
    }

    // Getter + Property (để bind TableView)
    public int getReqID() { return reqID.get(); }
    public IntegerProperty reqIDProperty() { return reqID; }

    public String getReqName() { return reqName.get(); }
    public StringProperty reqNameProperty() { return reqName; }

    public LocalDate getReqDate() { return reqDate.get(); }
    public ObjectProperty<LocalDate> reqDateProperty() { return reqDate; }

    public String getReqEmail() { return reqEmail.get(); }
    public StringProperty reqEmailProperty() { return reqEmail; }

    public String getReqType() { return reqType.get(); }
    public StringProperty reqTypeProperty() { return reqType; }

    public String getReqDetails() { return reqDetails.get(); }
    public StringProperty reqDetailsProperty() { return reqDetails; }
}
