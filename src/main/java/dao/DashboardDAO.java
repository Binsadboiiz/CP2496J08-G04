package dao;

import model.Product;
import model.TransactionDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    private final Connection conn;

    // Constructor dùng cho Controller truyền Connection
    public DashboardDAO(Connection conn) {
        this.conn = conn;
    }

    // Constructor tự tạo Connection nếu cần
    public DashboardDAO() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    public double getTotalRevenue() throws SQLException {
        String query = "SELECT SUM(TotalAmount) AS TotalRevenue FROM InvoiceDetail";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("TotalRevenue");
            }
        }
        return 0.0;
    }

    public int getInvoiceCount() throws SQLException {
        String query = "SELECT COUNT(*) AS InvoiceCount FROM Invoice";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("InvoiceCount");
            }
        }
        return 0;
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT ProductID, ProductCode, ProductName, Brand, Type, Price, Description, Image FROM Product";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("ProductID"),
                        rs.getString("ProductCode"),
                        rs.getString("ProductName"),
                        rs.getString("Brand"),
                        rs.getString("Type"),
                        rs.getDouble("Price"),
                        rs.getString("Description"),
                        rs.getString("Image")
                );
                products.add(product);
            }
        }
        return products;
    }

    public List<TransactionDetail> getRecentTransactions() throws SQLException {
        List<TransactionDetail> transactions = new ArrayList<>();
        String query = "SELECT i.InvoiceID, c.CustomerName, p.ProductName, d.PaymentMethod, id.Quantity, id.TotalAmount " +
                "FROM Invoice i " +
                "JOIN InvoiceDetail id ON i.InvoiceID = id.InvoiceID " +
                "JOIN Product p ON id.ProductID = p.ProductID " +
                "JOIN Customer c ON i.CustomerID = c.CustomerID " +
                "JOIN PaymentDetail d ON i.InvoiceID = d.InvoiceID " +
                "ORDER BY i.InvoiceDate DESC LIMIT 10";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TransactionDetail detail = new TransactionDetail(
                        rs.getString("ProductName"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("CustomerName"),
                        rs.getString("PaymentMethod"),
                        rs.getInt("Quantity")
                );
                transactions.add(detail);
            }
        }
        return transactions;
    }
}
