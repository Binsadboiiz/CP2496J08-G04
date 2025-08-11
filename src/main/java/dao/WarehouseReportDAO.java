package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseReportDAO {

    // ==================== BÁO CÁO TỒN KHO ====================

    /**
     * Báo cáo tổng quan tồn kho theo sản phẩm
     */
    public static List<InventoryReport> getInventoryReport() {
        List<InventoryReport> list = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price,
                   ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                   ISNULL(SUM(lrd.Quantity), 0) as TotalLoss,
                   ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM(lrd.Quantity), 0) as CurrentStock,
                   (ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM(lrd.Quantity), 0)) * p.Price as StockValue
            FROM Product p
            LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
            LEFT JOIN LossReportDetail lrd ON p.ProductID = lrd.ProductID
            GROUP BY p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price
            ORDER BY CurrentStock DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InventoryReport report = new InventoryReport();
                report.productID = rs.getInt("ProductID");
                report.productName = rs.getString("ProductName");
                report.productCode = rs.getString("ProductCode");
                report.brand = rs.getString("Brand");
                report.price = rs.getDouble("Price");
                report.totalReceived = rs.getInt("TotalReceived");
                report.totalLoss = rs.getInt("TotalLoss");
                report.currentStock = Math.max(0, rs.getInt("CurrentStock"));
                report.stockValue = rs.getDouble("StockValue");
                report.status = determineStockStatus(report.currentStock);
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Báo cáo sản phẩm sắp hết hàng
     */
    public static List<InventoryReport> getLowStockReport(int threshold) {
        List<InventoryReport> list = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price,
                   ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                   ISNULL(SUM(lrd.Quantity), 0) as TotalLoss,
                   ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM(lrd.Quantity), 0) as CurrentStock,
                   (ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM(lrd.Quantity), 0)) * p.Price as StockValue
            FROM Product p
            LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
            LEFT JOIN LossReportDetail lrd ON p.ProductID = lrd.ProductID
            GROUP BY p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price
            HAVING ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM(lrd.Quantity), 0) <= ?
            ORDER BY CurrentStock ASC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InventoryReport report = new InventoryReport();
                report.productID = rs.getInt("ProductID");
                report.productName = rs.getString("ProductName");
                report.productCode = rs.getString("ProductCode");
                report.brand = rs.getString("Brand");
                report.price = rs.getDouble("Price");
                report.totalReceived = rs.getInt("TotalReceived");
                report.totalLoss = rs.getInt("TotalLoss");
                report.currentStock = Math.max(0, rs.getInt("CurrentStock"));
                report.stockValue = rs.getDouble("StockValue");
                report.status = determineStockStatus(report.currentStock);
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== BÁO CÁO LỊCH SỬ NHẬP HÀNG ====================

    /**
     * Báo cáo lịch sử nhập hàng theo khoảng thời gian
     */
    public static List<StockEntryReport> getStockEntryReport(Date fromDate, Date toDate) {
        List<StockEntryReport> list = new ArrayList<>();
        String sql = """
        SELECT se.EntryID, se.Date, 
               COALESCE(s.Name, 'Unknown Supplier') as SupplierName, 
               COALESCE(emp.FullName, u.Username, 'Unknown Employee') as EmployeeName,
               COALESCE(SUM(sed.Quantity), 0) as TotalQuantity,
               COALESCE(SUM(sed.Quantity * sed.UnitCost), 0) as TotalValue,
               COUNT(sed.ProductID) as ProductCount
        FROM StockEntry se
        LEFT JOIN Supplier s ON se.SupplierID = s.SupplierID
        LEFT JOIN [User] u ON se.UserID = u.UserID
        LEFT JOIN Employee emp ON u.EmployeeID = emp.EmployeeID
        LEFT JOIN StockEntryDetail sed ON se.EntryID = sed.EntryID
        WHERE se.Date BETWEEN ? AND ?
        GROUP BY se.EntryID, se.Date, s.Name, u.Username, emp.FullName
        ORDER BY se.Date DESC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StockEntryReport report = new StockEntryReport();
                report.entryID = rs.getInt("EntryID");
                report.entryDate = rs.getDate("Date");
                report.supplierName = rs.getString("SupplierName");
                report.employeeName = rs.getString("EmployeeName");
                report.totalQuantity = rs.getInt("TotalQuantity");
                report.totalValue = rs.getDouble("TotalValue");
                report.productCount = rs.getInt("ProductCount");
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== BÁO CÁO TỔN THẤT - UPDATED TO USE LOSSREPORTDETAIL ====================

    /**
     * Báo cáo tổn thất theo khoảng thời gian - Lấy từ LossReportDetail
     */
    public static List<LossReport> getLossReport(Date fromDate, Date toDate) {
        List<LossReport> list = new ArrayList<>();
        String sql = """
        SELECT lrd.ReportID, 
               lr.ReportDate,
               COALESCE(emp.FullName, u.Username, 'Unknown Employee') as EmployeeName,
               SUM(lrd.Quantity) as TotalLossQuantity,
               SUM(lrd.Quantity * COALESCE(avgCost.AvgUnitCost, 0)) as TotalLossValue,
               COUNT(DISTINCT lrd.ProductID) as ProductCount
        FROM LossReportDetail lrd
        INNER JOIN LossReport lr ON lrd.ReportID = lr.ReportID
        LEFT JOIN [User] u ON lr.UserID = u.UserID
        LEFT JOIN Employee emp ON u.EmployeeID = emp.EmployeeID
        LEFT JOIN Product p ON lrd.ProductID = p.ProductID
        LEFT JOIN (
            SELECT sed.ProductID, 
                   CASE 
                       WHEN SUM(sed.Quantity) > 0 
                       THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                       ELSE 0 
                   END as AvgUnitCost
            FROM StockEntryDetail sed
            GROUP BY sed.ProductID
        ) avgCost ON p.ProductID = avgCost.ProductID
        WHERE lr.ReportDate BETWEEN ? AND ?
        GROUP BY lrd.ReportID, lr.ReportDate, u.Username, emp.FullName
        ORDER BY lr.ReportDate DESC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LossReport report = new LossReport();
                report.reportID = rs.getInt("ReportID");
                report.reportDate = rs.getDate("ReportDate");
                report.employeeName = rs.getString("EmployeeName");
                report.totalLossQuantity = rs.getInt("TotalLossQuantity");
                report.totalLossValue = rs.getDouble("TotalLossValue");
                report.productCount = rs.getInt("ProductCount");
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== BÁO CÁO CHI TIẾT TỔN THẤT ====================

    /**
     * Báo cáo chi tiết tổn thất theo ReportID
     */
    public static List<LossReportDetailView> getLossReportDetails(int reportID) {
        List<LossReportDetailView> list = new ArrayList<>();
        String sql = """
        SELECT lrd.ReportID, lrd.ProductID, lrd.Quantity, lrd.Note,
               p.ProductName, p.ProductCode, p.Brand,
               lr.ReportDate,
               COALESCE(emp.FullName, u.Username, 'Unknown Employee') as EmployeeName,
               CASE 
                   WHEN SUM(sed.Quantity) > 0 
                   THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                   ELSE 0 
               END as AvgUnitCost
        FROM LossReportDetail lrd
        INNER JOIN LossReport lr ON lrd.ReportID = lr.ReportID
        LEFT JOIN [User] u ON lr.UserID = u.UserID
        LEFT JOIN Employee emp ON u.EmployeeID = emp.EmployeeID
        LEFT JOIN Product p ON lrd.ProductID = p.ProductID
        LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
        WHERE lrd.ReportID = ?
        GROUP BY lrd.ReportID, lrd.ProductID, lrd.Quantity, lrd.Note,
                 p.ProductName, p.ProductCode, p.Brand, lr.ReportDate,
                 u.Username, emp.FullName
        ORDER BY p.ProductName
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LossReportDetailView detail = new LossReportDetailView();
                detail.reportID = rs.getInt("ReportID");
                detail.productID = rs.getInt("ProductID");
                detail.quantity = rs.getInt("Quantity");
                detail.note = rs.getString("Note");
                detail.productName = rs.getString("ProductName");
                detail.productCode = rs.getString("ProductCode");
                detail.brand = rs.getString("Brand");
                detail.reportDate = rs.getDate("ReportDate");
                detail.employeeName = rs.getString("EmployeeName");
                detail.avgUnitCost = rs.getDouble("AvgUnitCost");
                detail.lossValue = detail.quantity * detail.avgUnitCost;
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== BÁO CÁO THỐNG KÊ TỔNG HỢP - UPDATED ====================

    /**
     * Thống kê tổng quan kho hàng - Cập nhật để sử dụng LossReportDetail
     */
    public static WarehouseSummary getWarehouseSummary() {
        WarehouseSummary summary = new WarehouseSummary();

        String sql = """
            SELECT 
                COUNT(DISTINCT p.ProductID) as TotalProducts,
                ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                ISNULL(SUM(lrd.Quantity), 0) as TotalLoss,
                ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM(lrd.Quantity), 0) as CurrentStock,
                SUM((ISNULL(sed.Quantity, 0) - ISNULL(lrd.Quantity, 0)) * p.Price) as TotalStockValue,
                SUM(CASE WHEN (ISNULL(sed.Quantity, 0) - ISNULL(lrd.Quantity, 0)) = 0 THEN 1 ELSE 0 END) as OutOfStockCount,
                SUM(CASE WHEN (ISNULL(sed.Quantity, 0) - ISNULL(lrd.Quantity, 0)) BETWEEN 1 AND 5 THEN 1 ELSE 0 END) as LowStockCount
            FROM Product p
            LEFT JOIN (SELECT ProductID, SUM(Quantity) as Quantity FROM StockEntryDetail GROUP BY ProductID) sed 
                ON p.ProductID = sed.ProductID
            LEFT JOIN (SELECT ProductID, SUM(Quantity) as Quantity FROM LossReportDetail GROUP BY ProductID) lrd 
                ON p.ProductID = lrd.ProductID
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                summary.totalProducts = rs.getInt("TotalProducts");
                summary.totalReceived = rs.getInt("TotalReceived");
                summary.totalLoss = rs.getInt("TotalLoss");
                summary.currentStock = rs.getInt("CurrentStock");
                summary.totalStockValue = rs.getDouble("TotalStockValue");
                summary.outOfStockCount = rs.getInt("OutOfStockCount");
                summary.lowStockCount = rs.getInt("LowStockCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return summary;
    }

    // ==================== HELPER METHODS ====================

    private static String determineStockStatus(int currentStock) {
        if (currentStock == 0) {
            return "Hết hàng";
        } else if (currentStock <= 5) {
            return "Sắp hết";
        } else if (currentStock <= 10) {
            return "Ít hàng";
        } else {
            return "Đủ hàng";
        }
    }

    // ==================== INNER CLASSES ====================

    public static class InventoryReport {
        public int productID;
        public String productName;
        public String productCode;
        public String brand;
        public double price;
        public int totalReceived;
        public int totalLoss;
        public int currentStock;
        public double stockValue;
        public String status;

        // Getters for JavaFX binding
        public int getProductID() { return productID; }
        public String getProductName() { return productName; }
        public String getProductCode() { return productCode; }
        public String getBrand() { return brand; }
        public double getPrice() { return price; }
        public int getTotalReceived() { return totalReceived; }
        public int getTotalLoss() { return totalLoss; }
        public int getCurrentStock() { return currentStock; }
        public double getStockValue() { return stockValue; }
        public String getStatus() { return status; }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class StockEntryReport {
        public int entryID;
        public Date entryDate;
        public String supplierName;
        public String employeeName;
        public int totalQuantity;
        public double totalValue;
        public int productCount;

        // Getters for JavaFX binding
        public int getEntryID() { return entryID; }
        public Date getEntryDate() { return entryDate; }
        public String getSupplierName() { return supplierName; }
        public String getEmployeeName() { return employeeName; }
        public int getTotalQuantity() { return totalQuantity; }
        public double getTotalValue() { return totalValue; }
        public int getProductCount() { return productCount; }
    }

    public static class LossReport {
        public int reportID;
        public Date reportDate;
        public String employeeName;
        public int totalLossQuantity;
        public double totalLossValue;
        public int productCount;

        // Getters for JavaFX binding
        public int getReportID() { return reportID; }
        public Date getReportDate() { return reportDate; }
        public String getEmployeeName() { return employeeName; }
        public int getTotalLossQuantity() { return totalLossQuantity; }
        public double getTotalLossValue() { return totalLossValue; }
        public int getProductCount() { return productCount; }
    }

    // NEW: Chi tiết báo cáo tổn thất
    public static class LossReportDetailView {
        public int reportID;
        public int productID;
        public int quantity;
        public String note;
        public String productName;
        public String productCode;
        public String brand;
        public Date reportDate;
        public String employeeName;
        public double avgUnitCost;
        public double lossValue;

        // Getters for JavaFX binding
        public int getReportID() { return reportID; }
        public int getProductID() { return productID; }
        public int getQuantity() { return quantity; }
        public String getNote() { return note; }
        public String getProductName() { return productName; }
        public String getProductCode() { return productCode; }
        public String getBrand() { return brand; }
        public Date getReportDate() { return reportDate; }
        public String getEmployeeName() { return employeeName; }
        public double getAvgUnitCost() { return avgUnitCost; }
        public double getLossValue() { return lossValue; }
    }

    public static class WarehouseSummary {
        public int totalProducts;
        public int totalReceived;
        public int totalLoss;
        public int currentStock;
        public double totalStockValue;
        public int outOfStockCount;
        public int lowStockCount;

        // Getters for JavaFX binding
        public int getTotalProducts() { return totalProducts; }
        public int getTotalReceived() { return totalReceived; }
        public int getTotalLoss() { return totalLoss; }
        public int getCurrentStock() { return currentStock; }
        public double getTotalStockValue() { return totalStockValue; }
        public int getOutOfStockCount() { return outOfStockCount; }
        public int getLowStockCount() { return lowStockCount; }
    }
}