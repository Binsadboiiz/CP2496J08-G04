package dao;

import model.RevenueReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueReportDAO {
    private Connection conn;

    public RevenueReportDAO(Connection conn) {
        this.conn = conn;
    }

    public List<RevenueReport> getTodayRevenueByPaymentMethod() throws SQLException {
        String sql = """
            SELECT PaymentMethod, SUM(TotalAmount) AS Total 
            FROM Invoice 
            WHERE DATE(InvoiceDate) = CURDATE()
            GROUP BY PaymentMethod
        """;
        return executeQuery(sql);
    }

    public List<RevenueReport> getWeekRevenueByPaymentMethod() throws SQLException {
        String sql = """
            SELECT PaymentMethod, SUM(TotalAmount) AS Total 
            FROM Invoice 
            WHERE YEARWEEK(InvoiceDate, 1) = YEARWEEK(CURDATE(), 1)
            GROUP BY PaymentMethod
        """;
        return executeQuery(sql);
    }

    public List<RevenueReport> getMonthRevenueByPaymentMethod() throws SQLException {
        String sql = """
            SELECT PaymentMethod, SUM(TotalAmount) AS Total 
            FROM Invoice 
            WHERE MONTH(InvoiceDate) = MONTH(CURDATE()) AND YEAR(InvoiceDate) = YEAR(CURDATE())
            GROUP BY PaymentMethod
        """;
        return executeQuery(sql);
    }

    public List<RevenueReport> getYearRevenueByMonth() throws SQLException {
        String sql = """
            SELECT MONTH(InvoiceDate) AS MonthNumber, SUM(TotalAmount) AS Total
            FROM Invoice
            WHERE YEAR(InvoiceDate) = YEAR(CURDATE())
            GROUP BY MONTH(InvoiceDate)
            ORDER BY MonthNumber
        """;

        List<RevenueReport> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String monthLabel = "Tháng " + rs.getInt("MonthNumber");
                double total = rs.getDouble("Total");
                list.add(new RevenueReport(monthLabel, total));
            }
        }
        return list;
    }

    private List<RevenueReport> executeQuery(String sql) throws SQLException {
        List<RevenueReport> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String label = rs.getString(1);
                double total = rs.getDouble(2);
                list.add(new RevenueReport(label, total));
            }
        }
        return list;
    }
}
