package model;

public class RevenueReport {
    private String label; // Ví dụ: "Cash", "Credit Card" hoặc "Tháng 1", "Tháng 2", v.v.
    private double total;

    public RevenueReport(String label, double total) {
        this.label = label;
        this.total = total;
    }

    public String getLabel() {
        return label;
    }

    public double getTotal() {
        return total;
    }
}
