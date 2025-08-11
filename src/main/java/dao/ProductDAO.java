package dao;

import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private final Connection conn;

    // Constructor nhận Connection
    public ProductDAO(Connection conn) {
        this.conn = conn;
    }

    // 1. Lấy tất cả sản phẩm (STATIC)
    public static List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Product";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getString("ProductCode"),
                        rs.getString("Brand"),
                        rs.getString("Type"),
                        rs.getDouble("Price"),
                        rs.getString("Description"),
                        rs.getString("Image"),
                        rs.getString("CreatedAt"),
                        rs.getString("UpdatedAt")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy tất cả tên sản phẩm (Instance method)
    public List<String> getAllProductNames() {
        List<String> productNames = new ArrayList<>();
        String sql = "SELECT ProductName FROM Product";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                productNames.add(rs.getString("ProductName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productNames;
    }

    // 3. Lấy ProductID theo ProductName (Instance method)
    public int getProductIDByName(String productName) {
        String sql = "SELECT ProductID FROM Product WHERE ProductName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ProductID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    // 4. Thêm sản phẩm (STATIC)
    public static boolean insert(Product p) {
        String sql = """
                INSERT INTO Product(
                  ProductName, ProductCode, Brand, Type, Price, Description, Image
                ) VALUES(?,?,?,?,?,?,?)
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getProductName());
            ps.setString(2, p.getProductCode());
            ps.setString(3, p.getBrand());
            ps.setString(4, p.getType());
            ps.setDouble(5, p.getPrice());
            ps.setString(6, p.getDescription());
            ps.setString(7, p.getImage());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Xóa sản phẩm (STATIC)
    public static boolean delete(int productID) {
        String sql = "DELETE FROM Product WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. Tìm kiếm sản phẩm (STATIC)
    public static List<Product> search(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = """
                SELECT * FROM Product
                WHERE ProductName LIKE ? OR ProductCode LIKE ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(
                            rs.getInt("ProductID"),
                            rs.getString("ProductName"),
                            rs.getString("ProductCode"),
                            rs.getString("Brand"),
                            rs.getString("Type"),
                            rs.getDouble("Price"),
                            rs.getString("Description"),
                            rs.getString("Image"),
                            rs.getString("CreatedAt"),
                            rs.getString("UpdatedAt")
                    );
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 7. Cập nhật sản phẩm (STATIC)
    public static boolean update(Product product) {
        String sql = "UPDATE Product SET ProductName = ?, Brand = ?, Type = ?, Price = ?, Description = ?, Image = ?, UpdatedAt = GETDATE() WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getBrand());
            ps.setString(3, product.getType());
            ps.setDouble(4, product.getPrice());
            ps.setString(5, product.getDescription());
            ps.setString(6, product.getImage());
            ps.setInt(7, product.getProductID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 8. Lấy sản phẩm bán chạy (STATIC)
    public static List<Product> getTopSellingProducts() {
        List<Product> list = new ArrayList<>();
        String sql = """
                    SELECT TOP 5 p.ProductID, p.ProductName, p.Image, SUM(d.Quantity) AS Sales
                    FROM InvoiceDetail d
                    JOIN Product p ON d.ProductID = p.ProductID
                    GROUP BY p.ProductID, p.ProductName, p.Image
                    ORDER BY Sales DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setImage(rs.getString("Image"));
                p.setSales(rs.getInt("Sales"));
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 9. Tổng số sản phẩm (STATIC)
    public static int getTotalProducts() {
        String sql = "SELECT COUNT(*) FROM Product";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 10. Tìm sản phẩm theo tên (STATIC)
    public static Product getProductByName(String productName) {
        String sql = "SELECT * FROM Product WHERE ProductName = ?";
        Product product = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product(
                            rs.getInt("ProductID"),
                            rs.getString("ProductName"),
                            rs.getString("ProductCode"),
                            rs.getString("Brand"),
                            rs.getString("Type"),
                            rs.getDouble("Price"),
                            rs.getString("Description"),
                            rs.getString("Image"),
                            rs.getString("CreatedAt"),
                            rs.getString("UpdatedAt")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm sản phẩm theo tên: " + e.getMessage());
        }
        return product;
    }

    public String getProductNameByID(int appliedProductID) {
        String sql = "SELECT ProductName FROM Product WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appliedProductID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("ProductName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Nếu không tìm thấy
    }
    public Product getFirstProductByInvoiceID(int invoiceID) {
        String sql = "SELECT p.ProductID, p.ProductName, p.Image " +
                "FROM InvoiceDetail d JOIN Product p ON d.ProductID = p.ProductID " +
                "WHERE d.InvoiceID = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("ProductID"),
                            rs.getString("ProductName"),
                            rs.getString("ProductCode"),
                            rs.getString("Brand"),
                            rs.getString("Type"),
                            rs.getDouble("Price"),
                            rs.getString("Description"),
                            rs.getString("Image"),
                            rs.getString("CreatedAt"),
                            rs.getString("UpdatedAt")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 11. Lấy tất cả sản phẩm có thông tin tồn kho (STATIC)
    public static List<Product> getAllWithStock() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT *, ISNULL(StockQuantity, 0) as Stock FROM Product";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getString("ProductCode"),
                        rs.getString("Brand"),
                        rs.getString("Type"),
                        rs.getDouble("Price"),
                        rs.getString("Description"),
                        rs.getString("Image"),
                        rs.getString("CreatedAt"),
                        rs.getString("UpdatedAt"),
                        rs.getInt("Stock")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 12. Lấy số lượng tồn kho của sản phẩm (STATIC)
    public static int getProductStock(int productID) {
        String sql = "SELECT ISNULL(StockQuantity, 0) as Stock FROM Product WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Stock");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 13. Cập nhật tồn kho sau khi bán (STATIC)
    public static boolean updateStock(int productID, int soldQuantity) {
        String sql = "UPDATE Product SET StockQuantity = StockQuantity - ? WHERE ProductID = ? AND StockQuantity >= ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soldQuantity);
            ps.setInt(2, productID);
            ps.setInt(3, soldQuantity); // Đảm bảo tồn kho đủ
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 14. Tính tồn kho thực tế (nhập - xuất - hao hụt)
    public static int getRealTimeStock(int productID) {
        String sql = """
        SELECT 
            ISNULL(SUM(sed.Quantity), 0) - 
            ISNULL(SUM(id.Quantity), 0) - 
            ISNULL(SUM(lrd.Quantity), 0) as RealStock
        FROM Product p
        LEFT JOIN StockEntryDetail sed ON p.ProductID = sed.ProductID
        LEFT JOIN InvoiceDetail id ON p.ProductID = id.ProductID
        LEFT JOIN LossReportDetail lrd ON p.ProductID = lrd.ProductID
        WHERE p.ProductID = ?
        GROUP BY p.ProductID
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Math.max(0, rs.getInt("RealStock")); // Đảm bảo không âm
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
