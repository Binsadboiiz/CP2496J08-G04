package dao;

import model.LossReportDetail;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LossReportDetailDAO {

    public static boolean insertLossReportDetail(LossReportDetail detail) {
        String sql = "INSERT INTO LossReportDetail (ReportID, ProductID, Quantity, Note) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detail.getReportID());
            stmt.setInt(2, detail.getProductID());
            stmt.setInt(3, detail.getLostQuantity());
            stmt.setString(4, detail.getReason());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<LossReportDetail> getDetailsByReportID(int reportID) {
        List<LossReportDetail> details = new ArrayList<>();
        String sql = """
            SELECT lrd.ReportID, lrd.ProductID, lrd.Quantity, lrd.Note, lrd.LossDate,
                   p.ProductName, p.ProductCode, p.Price
            FROM LossReportDetail lrd
            LEFT JOIN Product p ON lrd.ProductID = p.ProductID
            WHERE lrd.ReportID = ?
            ORDER BY lrd.LossDate DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LossReportDetail detail = new LossReportDetail();
                detail.setReportID(rs.getInt("ReportID"));
                detail.setProductID(rs.getInt("ProductID"));
                detail.setLostQuantity(rs.getInt("Quantity"));
                detail.setReason(rs.getString("Note"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public static List<LossReportDetail> getAllLossReportDetails() {
        List<LossReportDetail> details = new ArrayList<>();
        String sql = """
            SELECT lrd.ReportID, lrd.ProductID, lrd.Quantity, lrd.Note, lrd.LossDate,
                   p.ProductName, p.ProductCode, p.Price,
                   lr.ReportDate, u.Username, e.FullName
            FROM LossReportDetail lrd
            LEFT JOIN Product p ON lrd.ProductID = p.ProductID
            LEFT JOIN LossReport lr ON lrd.ReportID = lr.ReportID
            LEFT JOIN [User] u ON lr.UserID = u.UserID
            LEFT JOIN Employee e ON u.EmployeeID = e.EmployeeID
            ORDER BY lrd.LossDate DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LossReportDetail detail = new LossReportDetail();
                detail.setReportID(rs.getInt("ReportID"));
                detail.setProductID(rs.getInt("ProductID"));
                detail.setLostQuantity(rs.getInt("Quantity"));
                detail.setReason(rs.getString("Note"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public static boolean updateLossReportDetail(LossReportDetail detail) {
        String sql = "UPDATE LossReportDetail SET ProductID = ?, Quantity = ?, Note = ? WHERE ReportID = ? AND ProductID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detail.getProductID());
            stmt.setInt(2, detail.getLostQuantity());
            stmt.setString(3, detail.getReason());
            stmt.setInt(4, detail.getReportID());
            stmt.setInt(5, detail.getProductID()); // Original ProductID for WHERE clause

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteLossReportDetail(int reportID, int productID) {
        String sql = "DELETE FROM LossReportDetail WHERE ReportID = ? AND ProductID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportID);
            stmt.setInt(2, productID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteAllDetailsByReportID(int reportID) {
        String sql = "DELETE FROM LossReportDetail WHERE ReportID = ?";

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
        String sql = "SELECT ISNULL(SUM(Quantity), 0) as TotalLoss FROM LossReportDetail WHERE ProductID = ?";

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

    public static double getTotalLossValueByProduct(int productID) {
        String sql = """
            SELECT ISNULL(SUM(lrd.Quantity * sed.UnitCost), 0) as TotalLossValue
            FROM LossReportDetail lrd
            JOIN StockEntryDetail sed ON lrd.ProductID = sed.ProductID
            WHERE lrd.ProductID = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("TotalLossValue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static List<LossReportDetail> getLossReportDetailsByDateRange(Date fromDate, Date toDate) {
        List<LossReportDetail> details = new ArrayList<>();
        String sql = """
            SELECT lrd.ReportID, lrd.ProductID, lrd.Quantity, lrd.Note, lrd.LossDate,
                   p.ProductName, p.ProductCode, p.Price
            FROM LossReportDetail lrd
            LEFT JOIN Product p ON lrd.ProductID = p.ProductID
            WHERE lrd.LossDate BETWEEN ? AND ?
            ORDER BY lrd.LossDate DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LossReportDetail detail = new LossReportDetail();
                detail.setReportID(rs.getInt("ReportID"));
                detail.setProductID(rs.getInt("ProductID"));
                detail.setLostQuantity(rs.getInt("Quantity"));
                detail.setReason(rs.getString("Note"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public static List<LossReportDetail> getTopLossProducts(int limit) {
        List<LossReportDetail> details = new ArrayList<>();
        String sql = """
            SELECT TOP (?) lrd.ProductID, p.ProductName, p.ProductCode,
                   SUM(lrd.Quantity) as TotalLossQuantity,
                   SUM(lrd.Quantity * ISNULL(sed.UnitCost, 0)) as TotalLossValue
            FROM LossReportDetail lrd
            JOIN Product p ON lrd.ProductID = p.ProductID
            LEFT JOIN StockEntryDetail sed ON lrd.ProductID = sed.ProductID
            GROUP BY lrd.ProductID, p.ProductName, p.ProductCode
            ORDER BY TotalLossQuantity DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LossReportDetail detail = new LossReportDetail();
                detail.setProductID(rs.getInt("ProductID"));
                detail.setLostQuantity(rs.getInt("TotalLossQuantity"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public static int getLossCountByReportID(int reportID) {
        String sql = "SELECT COUNT(*) as DetailCount FROM LossReportDetail WHERE ReportID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("DetailCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean existsLossReportDetail(int reportID, int productID) {
        String sql = "SELECT COUNT(*) as Count FROM LossReportDetail WHERE ReportID = ? AND ProductID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportID);
            stmt.setInt(2, productID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // CẬP NHẬT: Bao gồm ngày tạo báo cáo
    public static List<LossReportDetailExtended> getLossReportDetailsExtended() {
        List<LossReportDetailExtended> details = new ArrayList<>();
        String sql = """
            SELECT lrd.ReportID, lrd.ProductID, lrd.Quantity, lrd.Note, lrd.LossDate,
                   p.ProductName, p.ProductCode, p.Brand, p.Price,
                   lr.ReportDate,
                   ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                   CASE 
                       WHEN SUM(sed.Quantity) > 0 
                       THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                       ELSE 0 
                   END as AvgUnitCost
            FROM LossReportDetail lrd
            LEFT JOIN Product p ON lrd.ProductID = p.ProductID
            LEFT JOIN LossReport lr ON lrd.ReportID = lr.ReportID
            LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
            GROUP BY lrd.ReportID, lrd.ProductID, lrd.Quantity, lrd.Note, lrd.LossDate,
                     p.ProductName, p.ProductCode, p.Brand, p.Price,
                     lr.ReportDate
            ORDER BY lr.ReportDate DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LossReportDetailExtended detail = new LossReportDetailExtended();
                detail.reportID = rs.getInt("ReportID");
                detail.productID = rs.getInt("ProductID");
                detail.lostQuantity = rs.getInt("Quantity");
                detail.reason = rs.getString("Note");
                detail.productName = rs.getString("ProductName");
                detail.productCode = rs.getString("ProductCode");
                detail.brand = rs.getString("Brand");
                detail.price = rs.getDouble("Price");
                detail.avgUnitCost = rs.getDouble("AvgUnitCost");
                detail.employeeName = "Warehouse Staff";
                detail.totalReceived = rs.getInt("TotalReceived");
                detail.lossValue = detail.lostQuantity * detail.avgUnitCost;

                // THÊM: Lấy ngày tạo báo cáo
                Timestamp reportDate = rs.getTimestamp("ReportDate");
                detail.reportDate = reportDate;

                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    // Inner class for extended loss report detail information
    public static class LossReportDetailExtended {
        public int reportID;
        public int productID;
        public int lostQuantity;
        public String reason;
        public String productName;
        public String productCode;
        public String brand;
        public double price;
        public double avgUnitCost;
        public String employeeName;
        public int totalReceived;
        public double lossValue;
        public Timestamp reportDate; // THÊM: Ngày tạo báo cáo

        // Getters for JavaFX binding
        public int getReportID() { return reportID; }
        public int getProductID() { return productID; }
        public int getLostQuantity() { return lostQuantity; }
        public String getReason() { return reason; }
        public String getProductName() { return productName; }
        public String getProductCode() { return productCode; }
        public String getBrand() { return brand; }
        public double getPrice() { return price; }
        public double getAvgUnitCost() { return avgUnitCost; }
        public String getEmployeeName() { return "Warehouse Staff"; }
        public int getTotalReceived() { return totalReceived; }
        public double getLossValue() { return lossValue; }
        public int getRemainingQuantity() { return Math.max(0, totalReceived - lostQuantity); }

        // THÊM: Getter cho ngày tạo báo cáo
        public Timestamp getReportDate() { return reportDate; }

        // THÊM: Getter cho ngày tạo báo cáo đã format
        public String getReportDateFormatted() {
            if (reportDate != null) {
                return reportDate.toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            return "";
        }
    }
}