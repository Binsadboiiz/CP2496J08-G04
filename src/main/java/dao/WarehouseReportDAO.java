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
               COALESCE(u.Username, 'Unknown User') as Username,
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
                report.username = rs.getString("Username");
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

    /**
     * Báo cáo chi tiết nhập hàng theo sản phẩm trong khoảng thời gian
     */
    public static List<StockEntryDetailReport> getStockEntryDetailReport(Date fromDate, Date toDate) {
        List<StockEntryDetailReport> list = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.ProductName, p.ProductCode, p.Brand,
                   SUM(sed.Quantity) as TotalReceived,
                   AVG(sed.UnitCost) as AvgUnitCost,
                   SUM(sed.Quantity * sed.UnitCost) as TotalCost,
                   COUNT(DISTINCT se.EntryID) as EntryCount,
                   MIN(se.Date) as FirstEntryDate,
                   MAX(se.Date) as LastEntryDate
            FROM StockEntryDetail sed
            JOIN StockEntry se ON sed.EntryID = se.EntryID
            JOIN Product p ON sed.ProductID = p.ProductID
            WHERE se.Date BETWEEN ? AND ?
            GROUP BY p.ProductID, p.ProductName, p.ProductCode, p.Brand
            ORDER BY TotalReceived DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StockEntryDetailReport report = new StockEntryDetailReport();
                report.productID = rs.getInt("ProductID");
                report.productName = rs.getString("ProductName");
                report.productCode = rs.getString("ProductCode");
                report.brand = rs.getString("Brand");
                report.totalReceived = rs.getInt("TotalReceived");
                report.avgUnitCost = rs.getDouble("AvgUnitCost");
                report.totalCost = rs.getDouble("TotalCost");
                report.entryCount = rs.getInt("EntryCount");
                report.firstEntryDate = rs.getDate("FirstEntryDate");
                report.lastEntryDate = rs.getDate("LastEntryDate");
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== BÁO CÁO TỔN THẤT ====================

    /**
     * Báo cáo tổn thất theo khoảng thời gian
     */
    public static List<LossReport> getLossReport(Date fromDate, Date toDate) {
        List<LossReport> list = new ArrayList<>();
        String sql = """
        SELECT lr.ReportID, lr.ReportDate, u.Username, 
               COALESCE(emp.FullName, 'Warehouse Staff') as EmployeeName,
               SUM(lrd.Quantity) as TotalLossQuantity,
               SUM(lrd.Quantity * COALESCE(avgCost.AvgUnitCost, 0)) as TotalLossValue,
               COUNT(DISTINCT lrd.ProductID) as ProductCount,
               STRING_AGG(p.ProductCode + ': ' + CAST(lrd.Quantity AS VARCHAR), ', ') as ProductSummary
        FROM LossReport lr
        LEFT JOIN [User] u ON lr.UserID = u.UserID
        LEFT JOIN Employee emp ON u.EmployeeID = emp.EmployeeID
        LEFT JOIN LossReportDetail lrd ON lr.ReportID = lrd.ReportID
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
        GROUP BY lr.ReportID, lr.ReportDate, u.Username, emp.FullName
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
                report.username = rs.getString("Username");
                report.employeeName = rs.getString("EmployeeName");
                report.totalLossQuantity = rs.getInt("TotalLossQuantity");
                report.totalLossValue = rs.getDouble("TotalLossValue");
                report.productCount = rs.getInt("ProductCount");
                report.productSummary = rs.getString("ProductSummary");
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Báo cáo chi tiết tổn thất theo sản phẩm
     */
    public static List<LossDetailReport> getLossDetailReport(Date fromDate, Date toDate) {
        List<LossDetailReport> list = new ArrayList<>();
        String sql = """
        SELECT lrd.ReportID, lrd.ProductID, lrd.Quantity, lrd.Note as Reason,
               p.ProductName, p.ProductCode, p.Brand, p.Price,
               lr.ReportDate,
               COALESCE(emp.FullName, 'Warehouse Staff') as EmployeeName,
               ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
               CASE 
                   WHEN SUM(sed.Quantity) > 0 
                   THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                   ELSE 0 
               END as AvgUnitCost,
               lrd.Quantity * CASE 
                   WHEN SUM(sed.Quantity) > 0 
                   THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                   ELSE 0 
               END as LossValue
        FROM LossReportDetail lrd
        LEFT JOIN Product p ON lrd.ProductID = p.ProductID
        LEFT JOIN LossReport lr ON lrd.ReportID = lr.ReportID
        LEFT JOIN [User] u ON lr.UserID = u.UserID
        LEFT JOIN Employee emp ON u.EmployeeID = emp.EmployeeID
        LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
        WHERE lr.ReportDate BETWEEN ? AND ?
        GROUP BY lrd.ReportID, lrd.ProductID, lrd.Quantity, lrd.Note,
                 p.ProductName, p.ProductCode, p.Brand, p.Price,
                 lr.ReportDate, emp.FullName
        ORDER BY lr.ReportDate DESC, p.ProductCode ASC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LossDetailReport report = new LossDetailReport();
                report.reportID = rs.getInt("ReportID");
                report.productID = rs.getInt("ProductID");
                report.lostQuantity = rs.getInt("Quantity");
                report.reason = rs.getString("Reason");
                report.productName = rs.getString("ProductName");
                report.productCode = rs.getString("ProductCode");
                report.brand = rs.getString("Brand");
                report.price = rs.getDouble("Price");
                report.avgUnitCost = rs.getDouble("AvgUnitCost");
                report.reportDate = rs.getDate("ReportDate");
                report.employeeName = rs.getString("EmployeeName");
                report.totalReceived = rs.getInt("TotalReceived");
                report.lossValue = rs.getDouble("LossValue");
                report.remainingQuantity = Math.max(0, report.totalReceived - report.lostQuantity);
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Báo cáo sản phẩm bị tổn thất nhiều nhất - Sử dụng PreparedStatement an toàn
     */
    public static List<ProductLossReport> getTopLossProductsReport(int limit) {
        List<ProductLossReport> list = new ArrayList<>();

        // Validate limit to prevent issues
        if (limit <= 0 || limit > 1000) {
            limit = 10; // Default safe limit
        }

        String sql = """
    WITH RankedLossProducts AS (
        SELECT p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price,
               SUM(lrd.Quantity) as TotalLossQuantity,
               SUM(lrd.Quantity * COALESCE(avgCost.AvgUnitCost, 0)) as TotalLossValue,
               COUNT(DISTINCT lrd.ReportID) as LossReportCount,
               ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
               COALESCE(avgCost.AvgUnitCost, 0) as AvgUnitCost,
               CASE 
                   WHEN ISNULL(SUM(sed.Quantity), 0) > 0 
                   THEN (CAST(SUM(lrd.Quantity) AS FLOAT) * 100.0 / CAST(SUM(sed.Quantity) AS FLOAT))
                   ELSE 0
               END as LossPercentage,
               COUNT(DISTINCT CASE WHEN lrd.Note IS NOT NULL THEN lrd.Note END) as ReasonCount,
               ROW_NUMBER() OVER (ORDER BY SUM(lrd.Quantity) DESC) as RowNum
        FROM Product p
        JOIN LossReportDetail lrd ON p.ProductID = lrd.ProductID
        LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
        LEFT JOIN (
            SELECT sed2.ProductID, 
                   CASE 
                       WHEN SUM(sed2.Quantity) > 0 
                       THEN SUM(sed2.Quantity * sed2.UnitCost) / SUM(sed2.Quantity)
                       ELSE 0 
                   END as AvgUnitCost
            FROM StockEntryDetail sed2
            GROUP BY sed2.ProductID
        ) avgCost ON p.ProductID = avgCost.ProductID
        GROUP BY p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price, avgCost.AvgUnitCost
    )
    SELECT ProductID, ProductName, ProductCode, Brand, Price, AvgUnitCost,
           TotalLossQuantity, TotalLossValue, LossReportCount, TotalReceived,
           LossPercentage, ReasonCount
    FROM RankedLossProducts
    WHERE RowNum <= ?
    ORDER BY TotalLossQuantity DESC
""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductLossReport report = new ProductLossReport();
                report.productID = rs.getInt("ProductID");
                report.productName = rs.getString("ProductName");
                report.productCode = rs.getString("ProductCode");
                report.brand = rs.getString("Brand");
                report.price = rs.getDouble("Price");
                report.avgUnitCost = rs.getDouble("AvgUnitCost");
                report.totalLossQuantity = rs.getInt("TotalLossQuantity");
                report.totalLossValue = rs.getDouble("TotalLossValue");
                report.lossReportCount = rs.getInt("LossReportCount");
                report.totalReceived = rs.getInt("TotalReceived");
                report.lossPercentage = rs.getDouble("LossPercentage");
                report.reasonCount = rs.getInt("ReasonCount");
                report.commonReasons = ""; // Simplified - avoid complex string operations
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Báo cáo tổn thất theo lý do
     */
    public static List<LossReasonReport> getLossReasonReport(Date fromDate, Date toDate) {
        List<LossReasonReport> list = new ArrayList<>();
        String sql = """
        SELECT COALESCE(lrd.Note, 'Không rõ lý do') as Reason,
               COUNT(DISTINCT lrd.ProductID) as ProductCount,
               SUM(lrd.Quantity) as TotalQuantity,
               SUM(lrd.Quantity * COALESCE(avgCost.AvgUnitCost, 0)) as TotalValue,
               COUNT(DISTINCT lrd.ReportID) as ReportCount,
               AVG(COALESCE(avgCost.AvgUnitCost, 0)) as AvgUnitCost
        FROM LossReportDetail lrd
        LEFT JOIN Product p ON lrd.ProductID = p.ProductID
        LEFT JOIN LossReport lr ON lrd.ReportID = lr.ReportID
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
        GROUP BY COALESCE(lrd.Note, 'Không rõ lý do')
        ORDER BY TotalQuantity DESC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LossReasonReport report = new LossReasonReport();
                report.reason = rs.getString("Reason");
                report.productCount = rs.getInt("ProductCount");
                report.totalQuantity = rs.getInt("TotalQuantity");
                report.totalValue = rs.getDouble("TotalValue");
                report.reportCount = rs.getInt("ReportCount");
                report.avgUnitCost = rs.getDouble("AvgUnitCost");
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Báo cáo so sánh giá trị tổn thất theo giá nhập vs giá bán
     */
    public static List<LossValueComparisonReport> getLossValueComparisonReport(Date fromDate, Date toDate) {
        List<LossValueComparisonReport> list = new ArrayList<>();
        String sql = """
        SELECT p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price,
               SUM(lrd.Quantity) as TotalLossQuantity,
               COALESCE(avgCost.AvgUnitCost, 0) as AvgUnitCost,
               SUM(lrd.Quantity * p.Price) as LossValueBySellingPrice,
               SUM(lrd.Quantity * COALESCE(avgCost.AvgUnitCost, 0)) as LossValueByUnitCost,
               SUM(lrd.Quantity * p.Price) - SUM(lrd.Quantity * COALESCE(avgCost.AvgUnitCost, 0)) as ValueDifference,
               CASE 
                   WHEN SUM(lrd.Quantity * COALESCE(avgCost.AvgUnitCost, 0)) > 0
                   THEN ((SUM(lrd.Quantity * p.Price) - SUM(lrd.Quantity * COALESCE(avgCost.AvgUnitCost, 0))) * 100.0 
                         / SUM(lrd.Quantity * COALESCE(avgCost.AvgUnitCost, 0)))
                   ELSE 0
               END as PercentageDifference
        FROM LossReportDetail lrd
        LEFT JOIN Product p ON lrd.ProductID = p.ProductID
        LEFT JOIN LossReport lr ON lrd.ReportID = lr.ReportID
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
        GROUP BY p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price, avgCost.AvgUnitCost
        ORDER BY TotalLossQuantity DESC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LossValueComparisonReport report = new LossValueComparisonReport();
                report.productID = rs.getInt("ProductID");
                report.productName = rs.getString("ProductName");
                report.productCode = rs.getString("ProductCode");
                report.brand = rs.getString("Brand");
                report.sellingPrice = rs.getDouble("Price");
                report.avgUnitCost = rs.getDouble("AvgUnitCost");
                report.totalLossQuantity = rs.getInt("TotalLossQuantity");
                report.lossValueBySellingPrice = rs.getDouble("LossValueBySellingPrice");
                report.lossValueByUnitCost = rs.getDouble("LossValueByUnitCost");
                report.valueDifference = rs.getDouble("ValueDifference");
                report.percentageDifference = rs.getDouble("PercentageDifference");
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== BÁO CÁO THỐNG KÊ TỔNG HỢP ====================

    /**
     * Thống kê tổng quan kho hàng
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

        // Thêm thống kê về báo cáo tổn thất
        String lossSql = """
            SELECT COUNT(DISTINCT lr.ReportID) as TotalLossReports,
                   CASE WHEN COUNT(DISTINCT lr.ReportID) > 0 
                        THEN CAST(SUM(lrd.Quantity) AS FLOAT) / COUNT(DISTINCT lr.ReportID)
                        ELSE 0 END as AvgLossPerReport
            FROM LossReport lr
            LEFT JOIN LossReportDetail lrd ON lr.ReportID = lrd.ReportID
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(lossSql)) {

            if (rs.next()) {
                summary.totalLossReports = rs.getInt("TotalLossReports");
                summary.avgLossPerReport = rs.getDouble("AvgLossPerReport");
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
        public String username;
        public String employeeName;
        public int totalQuantity;
        public double totalValue;
        public int productCount;

        // Getters for JavaFX binding
        public int getEntryID() { return entryID; }
        public Date getEntryDate() { return entryDate; }
        public String getSupplierName() { return supplierName; }
        public String getUsername() { return username; }
        public String getEmployeeName() { return employeeName; }
        public int getTotalQuantity() { return totalQuantity; }
        public double getTotalValue() { return totalValue; }
        public int getProductCount() { return productCount; }
    }

    public static class StockEntryDetailReport {
        public int productID;
        public String productName;
        public String productCode;
        public String brand;
        public int totalReceived;
        public double avgUnitCost;
        public double totalCost;
        public int entryCount;
        public Date firstEntryDate;
        public Date lastEntryDate;

        // Getters for JavaFX binding
        public int getProductID() { return productID; }
        public String getProductName() { return productName; }
        public String getProductCode() { return productCode; }
        public String getBrand() { return brand; }
        public int getTotalReceived() { return totalReceived; }
        public double getAvgUnitCost() { return avgUnitCost; }
        public double getTotalCost() { return totalCost; }
        public int getEntryCount() { return entryCount; }
        public Date getFirstEntryDate() { return firstEntryDate; }
        public Date getLastEntryDate() { return lastEntryDate; }
    }

    public static class LossReport {
        public int reportID;
        public Date reportDate;
        public String username;
        public String employeeName;
        public int totalLossQuantity;
        public double totalLossValue;
        public int productCount;
        public String productSummary;

        // Getters for JavaFX binding
        public int getReportID() { return reportID; }
        public Date getReportDate() { return reportDate; }
        public String getUsername() { return username; }
        public String getEmployeeName() { return employeeName; }
        public int getTotalLossQuantity() { return totalLossQuantity; }
        public double getTotalLossValue() { return totalLossValue; }
        public int getProductCount() { return productCount; }
        public String getProductSummary() { return productSummary; }
    }

    public static class LossDetailReport {
        public int reportID;
        public int productID;
        public int lostQuantity;
        public String reason;
        public String productName;
        public String productCode;
        public String brand;
        public double price;
        public double avgUnitCost; // THÊM FIELD MỚI
        public Date reportDate;
        public String employeeName;
        public int totalReceived;
        public double lossValue;
        public int remainingQuantity;

        // Getters for JavaFX binding
        public int getReportID() { return reportID; }
        public int getProductID() { return productID; }
        public int getLostQuantity() { return lostQuantity; }
        public String getReason() { return reason; }
        public String getProductName() { return productName; }
        public String getProductCode() { return productCode; }
        public String getBrand() { return brand; }
        public double getPrice() { return price; }
        public double getAvgUnitCost() { return avgUnitCost; } // THÊM GETTER MỚI
        public Date getReportDate() { return reportDate; }
        public String getEmployeeName() { return employeeName; }
        public int getTotalReceived() { return totalReceived; }
        public double getLossValue() { return lossValue; }
        public int getRemainingQuantity() { return remainingQuantity; }
    }

    public static class ProductLossReport {
        public int productID;
        public String productName;
        public String productCode;
        public String brand;
        public double price;
        public double avgUnitCost; // THÊM FIELD MỚI
        public int totalLossQuantity;
        public double totalLossValue;
        public int lossReportCount;
        public int totalReceived;
        public double lossPercentage;
        public int reasonCount;
        public String commonReasons;

        // Getters for JavaFX binding
        public int getProductID() { return productID; }
        public String getProductName() { return productName; }
        public String getProductCode() { return productCode; }
        public String getBrand() { return brand; }
        public double getPrice() { return price; }
        public double getAvgUnitCost() { return avgUnitCost; } // THÊM GETTER MỚI
        public int getTotalLossQuantity() { return totalLossQuantity; }
        public double getTotalLossValue() { return totalLossValue; }
        public int getLossReportCount() { return lossReportCount; }
        public int getTotalReceived() { return totalReceived; }
        public double getLossPercentage() { return lossPercentage; }
        public int getReasonCount() { return reasonCount; }
        public String getCommonReasons() { return commonReasons; }
    }

    public static class LossReasonReport {
        public String reason;
        public int productCount;
        public int totalQuantity;
        public double totalValue;
        public int reportCount;
        public double avgUnitCost; // THÊM FIELD MỚI

        // Getters for JavaFX binding
        public String getReason() { return reason; }
        public int getProductCount() { return productCount; }
        public int getTotalQuantity() { return totalQuantity; }
        public double getTotalValue() { return totalValue; }
        public int getReportCount() { return reportCount; }
        public double getAvgUnitCost() { return avgUnitCost; } // THÊM GETTER MỚI
    }

    public static class LossValueComparisonReport {
        public int productID;
        public String productName;
        public String productCode;
        public String brand;
        public double sellingPrice;
        public double avgUnitCost;
        public int totalLossQuantity;
        public double lossValueBySellingPrice;
        public double lossValueByUnitCost;
        public double valueDifference;
        public double percentageDifference;

        // Getters for JavaFX binding
        public int getProductID() { return productID; }
        public String getProductName() { return productName; }
        public String getProductCode() { return productCode; }
        public String getBrand() { return brand; }
        public double getSellingPrice() { return sellingPrice; }
        public double getAvgUnitCost() { return avgUnitCost; }
        public int getTotalLossQuantity() { return totalLossQuantity; }
        public double getLossValueBySellingPrice() { return lossValueBySellingPrice; }
        public double getLossValueByUnitCost() { return lossValueByUnitCost; }
        public double getValueDifference() { return valueDifference; }
        public double getPercentageDifference() { return percentageDifference; }
    }

    public static class WarehouseSummary {
        public int totalProducts;
        public int totalReceived;
        public int totalLoss;
        public int currentStock;
        public double totalStockValue;
        public int outOfStockCount;
        public int lowStockCount;
        public int totalLossReports;
        public double avgLossPerReport;

        // Getters for JavaFX binding
        public int getTotalProducts() { return totalProducts; }
        public int getTotalReceived() { return totalReceived; }
        public int getTotalLoss() { return totalLoss; }
        public int getCurrentStock() { return currentStock; }
        public double getTotalStockValue() { return totalStockValue; }
        public int getOutOfStockCount() { return outOfStockCount; }
        public int getLowStockCount() { return lowStockCount; }
        public int getTotalLossReports() { return totalLossReports; }
        public double getAvgLossPerReport() { return avgLossPerReport; }
    }
}