package dao;

import model.RevenueReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueReportDAO {
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public static List<RevenueReport> getAllReports() {
        List<RevenueReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM RevenueReports";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                RevenueReport report = new RevenueReport(
                        rs.getInt("ReportID"),
                        rs.getString("ReportType"),
                        rs.getDate("ReportDate").toLocalDate(),
                        rs.getDouble("TotalRevenue"),
                        rs.getInt("TotalInvoices")
                );
                reports.add(report);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return reports;
    }
}
