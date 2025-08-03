package model;

import java.time.LocalDate;

public class RevenueReport {
    private int reportID;
    private String reportType;
    private LocalDate reportDate;
    private double totalRevenue;
    private int totalInvoices;

    public RevenueReport(int reportID, String reportType, LocalDate reportDate, double totalRevenue, int totalInvoices) {
        this.reportID = reportID;
        this.reportType = reportType;
        this.reportDate = reportDate;
        this.totalRevenue = totalRevenue;
        this.totalInvoices = totalInvoices;
    }

    // Getters and Setters
    public int getReportID() { return reportID; }
    public String getReportType() { return reportType; }
    public LocalDate getReportDate() { return reportDate; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getTotalInvoices() { return totalInvoices; }
}
