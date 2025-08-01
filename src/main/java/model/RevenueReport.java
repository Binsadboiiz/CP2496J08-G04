package model;

public class RevenueReport {
    private int reportID;
    private String reportType;
    private String reportDate;
    private double totalRevenue;
    private int totalInvoices;
    private String topSellingProduct;

    public RevenueReport(int reportID, String reportType, String reportDate, double totalRevenue, int totalInvoices, String topSellingProduct) {
        this.reportID = reportID;
        this.reportType = reportType;
        this.reportDate = reportDate;
        this.totalRevenue = totalRevenue;
        this.totalInvoices = totalInvoices;
        this.topSellingProduct = topSellingProduct;
    }

    // Getters
    public int getReportID() { return reportID; }
    public String getReportType() { return reportType; }
    public String getReportDate() { return reportDate; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getTotalInvoices() { return totalInvoices; }
    public String getTopSellingProduct() { return topSellingProduct; }
}
