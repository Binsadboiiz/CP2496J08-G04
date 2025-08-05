package model;

public class Customer {
    private int customerID;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private int loyaltyPoints;

    public Customer() {
    }

    // Thêm constructor ngắn để tiện tạo mới
    public Customer(String fullName, String phone, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    public Customer(int customerID, String fullName, String phone, String email, String address, int loyaltyPoints) {
        this.customerID = customerID;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.loyaltyPoints = loyaltyPoints;
    }

    // Getter - Setter
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}
