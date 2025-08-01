package dao;

import model.RevenueReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueReportsDAO {
    public static List<RevenueReport> getAllRevenueReports() {
        List<RevenueReport> list = new ArrayList<>();
        String sql = "SELECT rr.ReportID, rr.ReportType, rr.ReportDate, rr.TotalRevenue, rr.TotalInvoices, p.ProductName " +
                "FROM RevenueReports rr LEFT JOIN Product p ON rr.TopSellingProductID = p.ProductID";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                RevenueReport rr = new RevenueReport(
                        rs.getInt("ReportID"),
                        rs.getString("ReportType"),
                        rs.getString("ReportDate"),
                        rs.getDouble("TotalRevenue"),
                        rs.getInt("TotalInvoices"),
                        rs.getString("ProductName")
                );
                list.add(rr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
