package dao;

import model.StockEntryDetail;
import dao.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockEntryDetailDAO {
    public static List<StockEntryDetail> getByEntryID(int entryID) {
        List<StockEntryDetail> list = new ArrayList<>();
        String sql = """
            SELECT d.*, p.ProductName
            FROM StockEntryDetail d
            LEFT JOIN Product p ON d.ProductID = p.ProductID
            WHERE d.EntryID = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockEntryDetail detail = new StockEntryDetail();
                    detail.setEntryDetailID(rs.getInt("EntryDetailID"));
                    detail.setEntryID(rs.getInt("EntryID"));
                    detail.setProductID(rs.getInt("ProductID"));
                    detail.setQuantity(rs.getInt("Quantity"));
                    detail.setUnitCost(rs.getDouble("UnitCost"));
                    detail.setProductName(rs.getString("ProductName"));
                    list.add(detail);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static boolean insert(StockEntryDetail detail) {
        String sql = "INSERT INTO StockEntryDetail (EntryID, ProductID, Quantity, UnitCost) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getEntryID());
            ps.setInt(2, detail.getProductID());
            ps.setInt(3, detail.getQuantity());
            ps.setDouble(4, detail.getUnitCost());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static int getTotalReceivedByProduct(int productID) {
        String sql = "SELECT ISNULL(SUM(Quantity), 0) as TotalReceived FROM StockEntryDetail WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalReceived");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<StockEntryDetail> getAllProductsWithTotalReceived() {
        List<StockEntryDetail> list = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price,
                   ISNULL(SUM(sed.Quantity), 0) as TotalReceived
            FROM Product p
            LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
            GROUP BY p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price
            ORDER BY p.ProductName
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                StockEntryDetail detail = new StockEntryDetail();
                detail.setProductID(rs.getInt("ProductID"));
                detail.setProductName(rs.getString("ProductName"));
                detail.setQuantity(rs.getInt("TotalReceived"));
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<StockEntryDetail> getLowStockProducts(int threshold) {
        List<StockEntryDetail> list = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price,
                   ISNULL(SUM(sed.Quantity), 0) as TotalReceived,
                   ISNULL(SUM(lrd.Quantity), 0) as TotalLoss,
                   ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM(lrd.Quantity), 0) as CurrentStock
            FROM Product p
            LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
            LEFT JOIN LossReportDetail lrd ON p.ProductID = lrd.ProductID
            GROUP BY p.ProductID, p.ProductName, p.ProductCode, p.Brand, p.Price
            HAVING ISNULL(SUM(sed.Quantity), 0) - ISNULL(SUM(lrd.Quantity), 0) <= ?
            ORDER BY CurrentStock ASC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockEntryDetail detail = new StockEntryDetail();
                    detail.setProductID(rs.getInt("ProductID"));
                    detail.setProductName(rs.getString("ProductName"));
                    detail.setQuantity(rs.getInt("CurrentStock"));
                    list.add(detail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}