package model;

import java.time.LocalDateTime;

public class SalaryHistory {
    private int salaryID;
    private String employeeName;
    private int month;
    private int year;
    private double totalSalary;

    public SalaryHistory(int salaryID, String employeeName, int month, int year, double totalSalary) {
        this.salaryID = salaryID;
        this.employeeName = employeeName;
        this.month = month;
        this.year = year;
        this.totalSalary = totalSalary;
    }

    // Getters
    public int getSalaryID() { return salaryID; }
    public String getEmployeeName() { return employeeName; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public double getTotalSalary() { return totalSalary; }
}
