package model;

import java.sql.Date;

public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private Date registrationDate;

    public Customer(int id, String name, String phone, String email, Date registrationDate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
}
