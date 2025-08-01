package dao;

import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=CellPhoneStore;encrypt=false;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";


    /**
     * 1) Lấy tất cả sản phẩm
     */
    public static List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Product";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to DB", e);
        }
    }


    /**
     * 2) Thêm sản phẩm mới
     */
    public static boolean insert(Product p) {
        String sql = """
                INSERT INTO Product(
                  ProductName,
                  ProductCode,
                  Brand,
                  Type,
                  Price,
                  Description,
                  Image
                ) VALUES(?,?,?,?,?,?,?)
                """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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

    /**
     * 3) Xóa sản phẩm theo ID
     */
    public static boolean delete(int productID) {
        String sql = "DELETE FROM Product WHERE ProductID = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 4) Tìm sản phẩm theo từ khóa
     */
    public static List<Product> search(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = """
                SELECT * FROM Product
                WHERE ProductName LIKE ? OR ProductCode LIKE ?
                """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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

    public static List<Product> getTopSellingProducts() {
        List<Product> list = new ArrayList<>();
        String sql = """
    SELECT TOP 5 p.ProductID, p.ProductName, p.Image, SUM(d.Quantity) AS Sales
    FROM InvoiceDetail d
    JOIN Product p ON d.ProductID = p.ProductID
    GROUP BY p.ProductID, p.ProductName, p.Image
    ORDER BY Sales DESC
    """;


        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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
    public static int getTotalProducts() {
        String sql = "SELECT COUNT(*) FROM Product";
        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {e.printStackTrace(); return 0;}
    }
}
