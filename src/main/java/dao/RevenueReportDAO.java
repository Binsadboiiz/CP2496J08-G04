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

    // Doanh thu hôm nay theo phương thức thanh toán
    public List<RevenueReport> getTodayRevenueByPaymentMethod() throws SQLException {
        String sql = """
            SELECT p.PaymentMethod, SUM(p.Amount) AS Total
            FROM Payment p
            JOIN Invoice i ON p.InvoiceID = i.InvoiceID
            WHERE CONVERT(date, i.Date) = CONVERT(date, GETDATE())
            GROUP BY p.PaymentMethod
        """;
        return executeQuery(sql);
    }

    // Doanh thu tuần này theo phương thức thanh toán
    public List<RevenueReport> getWeekRevenueByPaymentMethod() throws SQLException {
        String sql = """
            SELECT p.PaymentMethod, SUM(p.Amount) AS Total
            FROM Payment p
            JOIN Invoice i ON p.InvoiceID = i.InvoiceID
            WHERE DATEPART(ISO_WEEK, i.Date) = DATEPART(ISO_WEEK, GETDATE())
              AND YEAR(i.Date) = YEAR(GETDATE())
            GROUP BY p.PaymentMethod
        """;
        return executeQuery(sql);
    }

    // Doanh thu tháng này theo phương thức thanh toán
    public List<RevenueReport> getMonthRevenueByPaymentMethod() throws SQLException {
        String sql = """
            SELECT p.PaymentMethod, SUM(p.Amount) AS Total
            FROM Payment p
            JOIN Invoice i ON p.InvoiceID = i.InvoiceID
            WHERE MONTH(i.Date) = MONTH(GETDATE()) AND YEAR(i.Date) = YEAR(GETDATE())
            GROUP BY p.PaymentMethod
        """;
        return executeQuery(sql);
    }

    // Doanh thu từng tháng trong năm nay (không phân biệt phương thức)
    public List<RevenueReport> getYearRevenueByMonth() throws SQLException {
        String sql = """
            SELECT MONTH(i.Date) AS MonthNumber, SUM(p.Amount) AS Total
            FROM Payment p
            JOIN Invoice i ON p.InvoiceID = i.InvoiceID
            WHERE YEAR(i.Date) = YEAR(GETDATE())
            GROUP BY MONTH(i.Date)
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
