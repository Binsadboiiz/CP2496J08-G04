package model;

import java.time.LocalDate;

public class SalaryHistory {
    private int employeeId;
    private double amount;
    private LocalDate date;

    public SalaryHistory(int employeeId, double amount, LocalDate date) {
        this.employeeId = employeeId;
        this.amount = amount;
        this.date = date;
    }

    // Getters and Setters
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
