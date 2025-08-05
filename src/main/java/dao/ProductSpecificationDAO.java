package dao;

import model.ProductSpecification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecificationDAO {

    /**
     * Lấy tất cả thông số kỹ thuật của một sản phẩm
     */
    public static List<ProductSpecification> getByProductId(int productId) {
        List<ProductSpecification> specifications = new ArrayList<>();
        String sql = "SELECT * FROM ProductSpecification WHERE ProductID = ? ORDER BY SpecificationName";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductSpecification spec = new ProductSpecification(
                            rs.getInt("SpecificationID"),
                            rs.getInt("ProductID"),
                            rs.getString("SpecificationName"),
                            rs.getString("SpecificationValue")
                    );
                    specifications.add(spec);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return specifications;
    }

    /**
     * Thêm thông số kỹ thuật mới
     */
    public static boolean insert(ProductSpecification specification) {
        String sql = "INSERT INTO ProductSpecification (ProductID, SpecificationName, SpecificationValue) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, specification.getProductID());
            ps.setString(2, specification.getSpecificationName());
            ps.setString(3, specification.getSpecificationValue());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông số kỹ thuật
     */
    public static boolean update(ProductSpecification specification) {
        String sql = "UPDATE ProductSpecification SET SpecificationName = ?, SpecificationValue = ? WHERE SpecificationID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, specification.getSpecificationName());
            ps.setString(2, specification.getSpecificationValue());
            ps.setInt(3, specification.getSpecificationID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa thông số kỹ thuật
     */
    public static boolean delete(int specificationId) {
        String sql = "DELETE FROM ProductSpecification WHERE SpecificationID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, specificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả thông số kỹ thuật của một sản phẩm
     */
    public static boolean deleteByProductId(int productId) {
        String sql = "DELETE FROM ProductSpecification WHERE ProductID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật hoặc thêm mới thông số kỹ thuật (upsert)
     */
    public static boolean upsertSpecification(int productId, String specName, String specValue) {
        // Kiểm tra xem thông số đã tồn tại chưa
        String checkSql = "SELECT SpecificationID FROM ProductSpecification WHERE ProductID = ? AND SpecificationName = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setInt(1, productId);
            checkPs.setString(2, specName);

            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    // Cập nhật thông số đã tồn tại
                    int specId = rs.getInt("SpecificationID");
                    ProductSpecification spec = new ProductSpecification(specId, productId, specName, specValue);
                    return update(spec);
                } else {
                    // Thêm mới thông số
                    ProductSpecification spec = new ProductSpecification(productId, specName, specValue);
                    return insert(spec);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy thông tin tồn kho của sản phẩm
     */
    public static int getInventoryQuantity(int productId) {
        String sql = "SELECT ISNULL(SUM(Quantity), 0) as TotalQuantity FROM Inventory WHERE ProductID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalQuantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy giá nhập mới nhất từ StockEntryDetail
     */
    public static Double getLatestCostPrice(int productId) {
        String sql = """
            SELECT TOP 1 sed.UnitCost 
            FROM StockEntryDetail sed 
            JOIN StockEntry se ON sed.EntryID = se.EntryID 
            WHERE sed.ProductID = ? 
            ORDER BY se.Date DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("UnitCost");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cập nhật thông tin chi tiết sản phẩm từ dữ liệu nhập hàng
     */
    public static boolean updateProductDetailsFromStock(int productId) {
        try {
            // Lấy thông tin tồn kho
            int quantity = getInventoryQuantity(productId);

            // Lấy giá nhập mới nhất
            Double costPrice = getLatestCostPrice(productId);

            // Cập nhật thông số tồn kho
            upsertSpecification(productId, "Tồn kho", String.valueOf(quantity));

            // Cập nhật giá nhập nếu có
            if (costPrice != null) {
                upsertSpecification(productId, "Giá nhập", String.format("%.0f VND", costPrice));
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}