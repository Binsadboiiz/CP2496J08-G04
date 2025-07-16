package model;

import java.time.LocalDate;

public class User {
    private String name;
    private LocalDate dob;
    private String phone;
    private String email;
    private String role;
    private String password;


    public User() {}

    public User(String name, LocalDate dob, String phone, String email, String role, String password) {
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    // Getters
    public String getName() {
        return name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // toString
    @Override
    public String toString() {
        return name + " - " + email + " (" + role + ")";
    }
}
