package dao;

import model.BestSellingProduct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BestSellingProductDAO {
    private final Connection conn;

    public BestSellingProductDAO(Connection conn) {
        this.conn = conn;
    }

    // Get Top N Best Selling Products
    public List<BestSellingProduct> getTopSellingProducts(int limit) throws SQLException {
        List<BestSellingProduct> bestSellingProducts = new ArrayList<>();
        String query = "SELECT p.ProductName, SUM(id.Quantity) AS TotalSales " +
                "FROM InvoiceDetail id " +
                "JOIN Product p ON id.ProductID = p.ProductID " +
                "GROUP BY p.ProductName " +
                "ORDER BY TotalSales DESC " +
                "LIMIT ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BestSellingProduct product = new BestSellingProduct(
                            rs.getString("ProductName"),
                            rs.getInt("TotalSales")
                    );
                    bestSellingProducts.add(product);
                }
            }
        }
        return bestSellingProducts;
    }
}
