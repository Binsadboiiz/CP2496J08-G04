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
    }

    // Lấy tất cả thông tin tồn kho
    public static List<InventorySummary> getAllInventorySummary() {
        List<InventorySummary> list = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.ProductCode, p.ProductName, p.Brand, p.Price,
                   ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                   ISNULL(SUM(lrd.Quantity), 0) as TotalLoss,
                   ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM(lrd.Quantity), 0) as CurrentStock
            FROM Product p
            LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
            LEFT JOIN LossReportDetail lrd ON p.ProductID = lrd.ProductID
            GROUP BY p.ProductID, p.ProductCode, p.ProductName, p.Brand, p.Price
            ORDER BY p.ProductName
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InventorySummary summary = new InventorySummary();
                summary.productID = rs.getInt("ProductID");
                summary.productCode = rs.getString("ProductCode");
                summary.productName = rs.getString("ProductName");
                summary.brand = rs.getString("Brand") != null ? rs.getString("Brand") : "N/A";
                summary.price = rs.getDouble("Price");
                summary.totalReceived = rs.getInt("TotalReceived");
                summary.totalLoss = rs.getInt("TotalLoss");
                summary.currentStock = Math.max(0, rs.getInt("CurrentStock"));
                summary.status = determineStockStatus(summary.currentStock);
                summary.value = summary.currentStock * summary.price;

                list.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Xác định trạng thái tồn kho
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

    // Lấy thống kê tổng quan
    public static InventoryStatistics getInventoryStatistics() {
        InventoryStatistics stats = new InventoryStatistics();
        String sql = """
            SELECT 
                COUNT(*) as TotalProducts,
                SUM(CASE WHEN (ISNULL(SUM_Received.Total, 0) - ISNULL(SUM_Loss.Total, 0)) = 0 THEN 1 ELSE 0 END) as OutOfStockItems,
                SUM(CASE WHEN (ISNULL(SUM_Received.Total, 0) - ISNULL(SUM_Loss.Total, 0)) BETWEEN 1 AND 10 THEN 1 ELSE 0 END) as LowStockItems,
                SUM((ISNULL(SUM_Received.Total, 0) - ISNULL(SUM_Loss.Total, 0)) * p.Price) as TotalValue
            FROM Product p
            LEFT JOIN (
                SELECT ProductID, SUM(Quantity) as Total 
                FROM StockEntryDetail 
                GROUP BY ProductID
            ) SUM_Received ON p.ProductID = SUM_Received.ProductID
            LEFT JOIN (
                SELECT ProductID, SUM(Quantity) as Total 
                FROM LossReportDetail 
                GROUP BY ProductID
            ) SUM_Loss ON p.ProductID = SUM_Loss.ProductID
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
        brands.add("Tất cả");

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
}