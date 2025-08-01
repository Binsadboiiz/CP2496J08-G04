package model;

public class Supplier {
    private int supplierID;
    private String name;
    private String contactName;
    private String phone;
    private String email;
    private String address;
    private String note;
    private String createdDate;
    private boolean isActive;

    // Constructor rỗng
    public Supplier() {
    }

    // Constructor đầy đủ
    public Supplier(int supplierID, String name, String contactName, String phone, String email, String address, String note, String createdDate, boolean isActive) {
        this.supplierID = supplierID;
        this.name = name;
        this.contactName = contactName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.note = note;
        this.createdDate = createdDate;
        this.isActive = isActive;
    }

    // Getter và Setter
    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
