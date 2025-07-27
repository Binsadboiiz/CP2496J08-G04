package model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Employee {
    private final IntegerProperty employeeID = new SimpleIntegerProperty();
    private final StringProperty fullName    = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateOfBirth = new SimpleObjectProperty<>();
    private final StringProperty idCard      = new SimpleStringProperty();
    private final StringProperty hometown    = new SimpleStringProperty();
    private final StringProperty phone       = new SimpleStringProperty();
    private final StringProperty email       = new SimpleStringProperty();
    private final StringProperty status      = new SimpleStringProperty();

    public Employee() {}

    // ID
    public int getEmployeeID()              { return employeeID.get(); }
    public void setEmployeeID(int id)       { this.employeeID.set(id); }
    public IntegerProperty employeeIDProperty() { return employeeID; }

    // FullName
    public String getFullName()             { return fullName.get(); }
    public void setFullName(String name)    { this.fullName.set(name); }
    public StringProperty fullNameProperty(){ return fullName; }

    // DateOfBirth
    public LocalDate getDateOfBirth()             { return dateOfBirth.get(); }
    public void setDateOfBirth(LocalDate dob)     { this.dateOfBirth.set(dob); }
    public ObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }

    // IDCard
    public String getIdCard()               { return idCard.get(); }
    public void setIdCard(String id)        { this.idCard.set(id); }
    public StringProperty idCardProperty()  { return idCard; }

    // Hometown
    public String getHometown()             { return hometown.get(); }
    public void setHometown(String h)       { this.hometown.set(h); }
    public StringProperty hometownProperty(){ return hometown; }

    // Phone
    public String getPhone()                { return phone.get(); }
    public void setPhone(String p)          { this.phone.set(p); }
    public StringProperty phoneProperty()   { return phone; }

    // Email
    public String getEmail()                { return email.get(); }
    public void setEmail(String e)          { this.email.set(e); }
    public StringProperty emailProperty()   { return email; }

    // Status
    public String getStatus()               { return status.get(); }
    public void setStatus(String s)         { this.status.set(s); }
    public StringProperty statusProperty()  { return status; }
}
