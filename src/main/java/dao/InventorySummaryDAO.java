package dao;

import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventorySummaryDAO {

    // Class để chứa thông tin tồn kho
    public static class InventorySummary {
        public int productID;
        public String productCode;
        public String productName;
        public String brand;
        public double price;
        public int totalReceived;
        public int totalLoss;
        public int currentStock;
        public String status;
        public double value;
        public double avgUnitCost; // Thêm giá nhập trung bình

        // Getters cho JavaFX binding
        public int getProductID() { return productID; }
        public String getProductCode() { return productCode; }
        public String getProductName() { return productName; }
        public String getBrand() { return brand; }
        public double getPrice() { return price; }
        public int getTotalReceived() { return totalReceived; }
        public int getTotalLoss() { return totalLoss; }
        public int getCurrentStock() { return currentStock; }
        public String getStatus() { return status; }
        public double getValue() { return value; }
        public double getAvgUnitCost() { return avgUnitCost; }
    }

    // Lấy tất cả thông tin tồn kho với logic cải tiến
    public static List<InventorySummary> getAllInventorySummary() {
        List<InventorySummary> list = new ArrayList<>();
        String sql = """
            WITH StockSummary AS (
                SELECT 
                    p.ProductID,
                    p.ProductCode,
                    p.ProductName,
                    ISNULL(p.Brand, 'N/A') as Brand,
                    p.Price,
                    -- Tính tổng số lượng nhập
                    ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                    -- Tính giá nhập trung bình có trọng số
                    CASE 
                        WHEN SUM(sed.Quantity) > 0 
                        THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                        ELSE 0 
                    END as AvgUnitCost
                FROM Product p
                LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
                GROUP BY p.ProductID, p.ProductCode, p.ProductName, p.Brand, p.Price
            ),
            LossSummary AS (
                SELECT 
                    ProductID,
                    ISNULL(SUM(Quantity), 0) as TotalLoss
                FROM LossReportDetail
                GROUP BY ProductID
            )
            SELECT 
                ss.ProductID,
                ss.ProductCode,
                ss.ProductName,
                ss.Brand,
                ss.Price,
                ss.TotalReceived,
                ss.AvgUnitCost,
                ISNULL(ls.TotalLoss, 0) as TotalLoss,
                -- Tính tồn kho thực tế
                CASE 
                    WHEN ss.TotalReceived - ISNULL(ls.TotalLoss, 0) < 0 
                    THEN 0 
                    ELSE ss.TotalReceived - ISNULL(ls.TotalLoss, 0)
                END as CurrentStock
            FROM StockSummary ss
            LEFT JOIN LossSummary ls ON ss.ProductID = ls.ProductID
            ORDER BY ss.ProductName
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InventorySummary summary = new InventorySummary();
                summary.productID = rs.getInt("ProductID");
                summary.productCode = rs.getString("ProductCode");
                summary.productName = rs.getString("ProductName");
                summary.brand = rs.getString("Brand");
                summary.price = rs.getDouble("Price");
                summary.totalReceived = rs.getInt("TotalReceived");
                summary.totalLoss = rs.getInt("TotalLoss");
                summary.currentStock = Math.max(0, rs.getInt("CurrentStock"));
                summary.avgUnitCost = rs.getDouble("AvgUnitCost");
                summary.status = determineStockStatus(summary.currentStock);
                // Tính giá trị tồn kho dựa trên giá nhập trung bình
                summary.value = summary.currentStock * summary.avgUnitCost;

                list.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Xác định trạng thái tồn kho với các ngưỡng có thể cấu hình
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

    // Lấy thông tin tồn kho cho một sản phẩm cụ thể
    public static InventorySummary getInventorySummaryByProductId(int productId) {
        String sql = """
            WITH StockSummary AS (
                SELECT 
                    p.ProductID,
                    p.ProductCode,
                    p.ProductName,
                    ISNULL(p.Brand, 'N/A') as Brand,
                    p.Price,
                    ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                    CASE 
                        WHEN SUM(sed.Quantity) > 0 
                        THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                        ELSE 0 
                    END as AvgUnitCost
                FROM Product p
                LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
                WHERE p.ProductID = ?
                GROUP BY p.ProductID, p.ProductCode, p.ProductName, p.Brand, p.Price
            ),
            LossSummary AS (
                SELECT 
                    ProductID,
                    ISNULL(SUM(Quantity), 0) as TotalLoss
                FROM LossReportDetail
                WHERE ProductID = ?
                GROUP BY ProductID
            )
            SELECT 
                ss.ProductID,
                ss.ProductCode,
                ss.ProductName,
                ss.Brand,
                ss.Price,
                ss.TotalReceived,
                ss.AvgUnitCost,
                ISNULL(ls.TotalLoss, 0) as TotalLoss,
                CASE 
                    WHEN ss.TotalReceived - ISNULL(ls.TotalLoss, 0) < 0 
                    THEN 0 
                    ELSE ss.TotalReceived - ISNULL(ls.TotalLoss, 0)
                END as CurrentStock
            FROM StockSummary ss
            LEFT JOIN LossSummary ls ON ss.ProductID = ls.ProductID
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                InventorySummary summary = new InventorySummary();
                summary.productID = rs.getInt("ProductID");
                summary.productCode = rs.getString("ProductCode");
                summary.productName = rs.getString("ProductName");
                summary.brand = rs.getString("Brand");
                summary.price = rs.getDouble("Price");
                summary.totalReceived = rs.getInt("TotalReceived");
                summary.totalLoss = rs.getInt("TotalLoss");
                summary.currentStock = Math.max(0, rs.getInt("CurrentStock"));
                summary.avgUnitCost = rs.getDouble("AvgUnitCost");
                summary.status = determineStockStatus(summary.currentStock);
                summary.value = summary.currentStock * summary.avgUnitCost;
                return summary;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy thống kê tổng quan với logic cải tiến
    public static InventoryStatistics getInventoryStatistics() {
        InventoryStatistics stats = new InventoryStatistics();
        String sql = """
            WITH StockCalculation AS (
                SELECT 
                    p.ProductID,
                    p.Price,
                    ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                    ISNULL(SUM_Loss.TotalLoss, 0) as TotalLoss,
                    CASE 
                        WHEN ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM_Loss.TotalLoss, 0) < 0 
                        THEN 0 
                        ELSE ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM_Loss.TotalLoss, 0)
                    END as CurrentStock,
                    CASE 
                        WHEN SUM(sed.Quantity) > 0 
                        THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                        ELSE 0 
                    END as AvgUnitCost
                FROM Product p
                LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
                LEFT JOIN (
                    SELECT ProductID, SUM(Quantity) as TotalLoss 
                    FROM LossReportDetail 
                    GROUP BY ProductID
                ) SUM_Loss ON p.ProductID = SUM_Loss.ProductID
                GROUP BY p.ProductID, p.Price, SUM_Loss.TotalLoss
            )
            SELECT 
                COUNT(*) as TotalProducts,
                SUM(CASE WHEN CurrentStock = 0 THEN 1 ELSE 0 END) as OutOfStockItems,
                SUM(CASE WHEN CurrentStock BETWEEN 1 AND 10 THEN 1 ELSE 0 END) as LowStockItems,
                SUM(CurrentStock * AvgUnitCost) as TotalValue
            FROM StockCalculation
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                stats.totalProducts = rs.getInt("TotalProducts");
                stats.outOfStockItems = rs.getInt("OutOfStockItems");
                stats.lowStockItems = rs.getInt("LowStockItems");
                stats.totalValue = rs.getDouble("TotalValue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Class để chứa thống kê
    public static class InventoryStatistics {
        public int totalProducts;
        public int outOfStockItems;
        public int lowStockItems;
        public double totalValue;

        public int getTotalProducts() { return totalProducts; }
        public int getOutOfStockItems() { return outOfStockItems; }
        public int getLowStockItems() { return lowStockItems; }
        public double getTotalValue() { return totalValue; }
    }

    // Lấy danh sách thương hiệu distinct
    public static List<String> getAllBrands() {
        List<String> brands = new ArrayList<>();
        String sql = "SELECT DISTINCT Brand FROM Product WHERE Brand IS NOT NULL ORDER BY Brand";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String brand = rs.getString("Brand");
                if (brand != null && !brand.trim().isEmpty()) {
                    brands.add(brand);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return brands;
    }

    // Phương thức mới: Lấy sản phẩm cần chú ý (hết hàng hoặc sắp hết)
    public static List<InventorySummary> getCriticalStockItems() {
        List<InventorySummary> list = new ArrayList<>();
        String sql = """
            WITH StockSummary AS (
                SELECT 
                    p.ProductID,
                    p.ProductCode,
                    p.ProductName,
                    ISNULL(p.Brand, 'N/A') as Brand,
                    p.Price,
                    ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                    CASE 
                        WHEN SUM(sed.Quantity) > 0 
                        THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                        ELSE 0 
                    END as AvgUnitCost
                FROM Product p
                LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
                GROUP BY p.ProductID, p.ProductCode, p.ProductName, p.Brand, p.Price
            ),
            LossSummary AS (
                SELECT 
                    ProductID,
                    ISNULL(SUM(Quantity), 0) as TotalLoss
                FROM LossReportDetail
                GROUP BY ProductID
            )
            SELECT 
                ss.ProductID,
                ss.ProductCode,
                ss.ProductName,
                ss.Brand,
                ss.Price,
                ss.TotalReceived,
                ss.AvgUnitCost,
                ISNULL(ls.TotalLoss, 0) as TotalLoss,
                CASE 
                    WHEN ss.TotalReceived - ISNULL(ls.TotalLoss, 0) < 0 
                    THEN 0 
                    ELSE ss.TotalReceived - ISNULL(ls.TotalLoss, 0)
                END as CurrentStock
            FROM StockSummary ss
            LEFT JOIN LossSummary ls ON ss.ProductID = ls.ProductID
            WHERE (ss.TotalReceived - ISNULL(ls.TotalLoss, 0)) <= 5  -- Chỉ lấy sản phẩm có stock <= 5
            ORDER BY CurrentStock ASC, ss.ProductName
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InventorySummary summary = new InventorySummary();
                summary.productID = rs.getInt("ProductID");
                summary.productCode = rs.getString("ProductCode");
                summary.productName = rs.getString("ProductName");
                summary.brand = rs.getString("Brand");
                summary.price = rs.getDouble("Price");
                summary.totalReceived = rs.getInt("TotalReceived");
                summary.totalLoss = rs.getInt("TotalLoss");
                summary.currentStock = Math.max(0, rs.getInt("CurrentStock"));
                summary.avgUnitCost = rs.getDouble("AvgUnitCost");
                summary.status = determineStockStatus(summary.currentStock);
                summary.value = summary.currentStock * summary.avgUnitCost;

                list.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}