package dao;

import model.LossReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LossReportDAO {

    public static int insertLossReport(LossReport report) {
        String sql = "INSERT INTO LossReport (UserID, ReportDate) OUTPUT INSERTED.ReportID VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, report.getEmployeeID()); // Using EmployeeID as UserID
            stmt.setTimestamp(2, report.getReportDate());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ReportID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<LossReport> getAllLossReports() {
        List<LossReport> reports = new ArrayList<>();
        String sql = """
            SELECT lr.ReportID, lr.UserID, lr.ReportDate, 
                   u.Username, e.FullName
            FROM LossReport lr
            LEFT JOIN [User] u ON lr.UserID = u.UserID
            LEFT JOIN Employee e ON u.EmployeeID = e.EmployeeID
            ORDER BY lr.ReportDate DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LossReport report = new LossReport();
                report.setReportID(rs.getInt("ReportID"));
                report.setEmployeeID(rs.getInt("UserID"));
                report.setReportDate(rs.getTimestamp("ReportDate"));
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public static LossReport getLossReportById(int reportID) {
        String sql = """
            SELECT lr.ReportID, lr.UserID, lr.ReportDate,
                   u.Username, e.FullName
            FROM LossReport lr
            LEFT JOIN [User] u ON lr.UserID = u.UserID
            LEFT JOIN Employee e ON u.EmployeeID = e.EmployeeID
            WHERE lr.ReportID = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LossReport report = new LossReport();
                report.setReportID(rs.getInt("ReportID"));
                report.setEmployeeID(rs.getInt("UserID"));
                report.setReportDate(rs.getTimestamp("ReportDate"));
                return report;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateLossReport(LossReport report) {
        String sql = "UPDATE LossReport SET UserID = ?, ReportDate = ? WHERE ReportID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, report.getEmployeeID());
            stmt.setTimestamp(2, report.getReportDate());
            stmt.setInt(3, report.getReportID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteLossReport(int reportID) {
        String sql = "DELETE FROM LossReport WHERE ReportID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getTotalLossQuantityByProduct(int productID) {
        String sql = """
            SELECT ISNULL(SUM(lrd.Quantity), 0) as TotalLoss
            FROM LossReportDetail lrd
            WHERE lrd.ProductID = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("TotalLoss");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalLossQuantityByEntryDetail(int entryDetailID) {
        String sql = """
            SELECT ISNULL(SUM(lrd.Quantity), 0) as TotalLoss
            FROM LossReportDetail lrd
            JOIN StockEntryDetail sed ON lrd.ProductID = sed.ProductID
            WHERE sed.EntryDetailID = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entryDetailID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("TotalLoss");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<LossReport> getLossReportsByDateRange(Date fromDate, Date toDate) {
        List<LossReport> reports = new ArrayList<>();
        String sql = """
            SELECT lr.ReportID, lr.UserID, lr.ReportDate
            FROM LossReport lr
            WHERE lr.ReportDate BETWEEN ? AND ?
            ORDER BY lr.ReportDate DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LossReport report = new LossReport();
                report.setReportID(rs.getInt("ReportID"));
                report.setEmployeeID(rs.getInt("UserID"));
                report.setReportDate(rs.getTimestamp("ReportDate"));
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    // THÊM: Phương thức tính tổng giá trị tổn thất dựa trên giá nhập
    public static double getTotalLossValueBasedOnUnitCost() {
        String sql = """
            SELECT ISNULL(SUM(lrd.Quantity * avgCost.AvgUnitCost), 0) as TotalLossValue
            FROM LossReportDetail lrd
            JOIN (
                SELECT sed.ProductID, 
                       CASE 
                           WHEN SUM(sed.Quantity) > 0 
                           THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                           ELSE 0 
                       END as AvgUnitCost
                FROM StockEntryDetail sed
                GROUP BY sed.ProductID
            ) avgCost ON lrd.ProductID = avgCost.ProductID
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("TotalLossValue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // THÊM: Phương thức thống kê tổn thất theo tháng dựa trên giá nhập
    public static List<MonthlyLossReport> getMonthlyLossReportsBasedOnUnitCost() {
        List<MonthlyLossReport> reports = new ArrayList<>();
        String sql = """
            SELECT YEAR(lr.ReportDate) as Year, 
                   MONTH(lr.ReportDate) as Month,
                   SUM(lrd.Quantity) as TotalQuantity,
                   SUM(lrd.Quantity * avgCost.AvgUnitCost) as TotalValue
            FROM LossReport lr
            JOIN LossReportDetail lrd ON lr.ReportID = lrd.ReportID
            JOIN (
                SELECT sed.ProductID, 
                       CASE 
                           WHEN SUM(sed.Quantity) > 0 
                           THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                           ELSE 0 
                       END as AvgUnitCost
                FROM StockEntryDetail sed
                GROUP BY sed.ProductID
            ) avgCost ON lrd.ProductID = avgCost.ProductID
            GROUP BY YEAR(lr.ReportDate), MONTH(lr.ReportDate)
            ORDER BY Year DESC, Month DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MonthlyLossReport report = new MonthlyLossReport();
                report.year = rs.getInt("Year");
                report.month = rs.getInt("Month");
                report.totalQuantity = rs.getInt("TotalQuantity");
                report.totalValue = rs.getDouble("TotalValue");
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    // THÊM: Inner class cho báo cáo tổn thất hàng tháng
    public static class MonthlyLossReport {
        public int year;
        public int month;
        public int totalQuantity;
        public double totalValue;

        public int getYear() { return year; }
        public int getMonth() { return month; }
        public int getTotalQuantity() { return totalQuantity; }
        public double getTotalValue() { return totalValue; }
        public String getMonthYear() { return month + "/" + year; }
    }
}