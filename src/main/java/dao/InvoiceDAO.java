package dao;

import model.Invoice;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.sql.Date;


public class InvoiceDAO {

    public int insertInvoice(Invoice invoice) {
        String sql = "INSERT INTO Invoice (CustomerID, UserID, Date, TotalAmount, Discount, Status, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, invoice.getCustomerID());
            stmt.setInt(2, invoice.getUserID());
            stmt.setTimestamp(3, Timestamp.valueOf(invoice.getDate()));
            stmt.setBigDecimal(4, invoice.getTotalAmount());
            stmt.setBigDecimal(5, invoice.getDiscount());
            stmt.setString(6, invoice.getStatus());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // return generated InvoiceID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Invoice> getAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM Invoice ORDER BY Date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceID(rs.getInt("InvoiceID"));
                inv.setCustomerID(rs.getInt("CustomerID"));
                inv.setUserID(rs.getInt("UserID"));
                inv.setDate(rs.getTimestamp("Date").toLocalDateTime());
                inv.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                inv.setDiscount(rs.getBigDecimal("Discount"));
                inv.setStatus(rs.getString("Status"));
                list.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int countInvoices() {
        String sql = "SELECT COUNT(*) FROM Invoice";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getTodaysSales() {
        String sql = ""
                + "SELECT ISNULL(SUM(TotalAmount), 0) "
                + "FROM Invoice "
                + "WHERE CONVERT(date, Date) = CONVERT(date, GETDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public List<Invoice> searchInvoices(String keyword) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.* FROM Invoice i " +
                "JOIN Customer c ON i.CustomerID = c.CustomerID " +
                "WHERE c.FullName LIKE ? " +
                "OR EXISTS (SELECT 1 FROM InvoiceDetail d " +
                "JOIN Product p ON d.ProductID = p.ProductID " +
                "WHERE d.InvoiceID = i.InvoiceID AND p.ProductName LIKE ?) " +
                "ORDER BY i.Date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceID(rs.getInt("InvoiceID"));
                inv.setCustomerID(rs.getInt("CustomerID"));
                inv.setUserID(rs.getInt("UserID"));
                inv.setDate(rs.getTimestamp("Date").toLocalDateTime());
                inv.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                inv.setDiscount(rs.getBigDecimal("Discount"));
                inv.setStatus(rs.getString("Status"));
                list.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Invoice> filterInvoices(String customerName, String productName, LocalDate from, LocalDate to) {
        List<Invoice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT i.* FROM Invoice i " +
                        "LEFT JOIN Customer c ON i.CustomerID = c.CustomerID " +
                        "LEFT JOIN InvoiceDetail d ON i.InvoiceID = d.InvoiceID " +
                        "LEFT JOIN Product p ON d.ProductID = p.ProductID WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (customerName != null && !customerName.isEmpty()) {
            sql.append(" AND c.FullName LIKE ?");
            params.add("%" + customerName + "%");
        }
        if (productName != null && !productName.isEmpty()) {
            sql.append(" AND p.ProductName LIKE ?");
            params.add("%" + productName + "%");
        }
        if (from != null) {
            sql.append(" AND i.Date >= ?");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND i.Date <= ?");
            params.add(java.sql.Date.valueOf(to));
        }

        sql.append(" ORDER BY i.Date DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceID(rs.getInt("InvoiceID"));
                inv.setCustomerID(rs.getInt("CustomerID"));
                inv.setUserID(rs.getInt("UserID"));
                inv.setDate(rs.getTimestamp("Date").toLocalDateTime());
                inv.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                inv.setDiscount(rs.getBigDecimal("Discount"));
                inv.setStatus(rs.getString("Status"));
                list.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Invoice> getAllInvoicesWithCustomerName() {
        List<Invoice> list = new ArrayList<>();
        String sql = """
            SELECT i.*, c.FullName
            FROM Invoice i
            LEFT JOIN Customer c ON i.CustomerID = c.CustomerID
            ORDER BY i.Date DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceID(rs.getInt("InvoiceID"));
                inv.setCustomerID(rs.getInt("CustomerID"));
                inv.setUserID(rs.getInt("UserID"));
                inv.setDate(rs.getTimestamp("Date").toLocalDateTime());
                inv.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                inv.setDiscount(rs.getBigDecimal("Discount"));
                inv.setStatus(rs.getString("Status"));
                inv.setCustomerName(rs.getString("FullName"));
                list.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== THÊM METHODS MỚI - KHÔNG SỬA CODE CŨ =====

    // Lấy tất cả hóa đơn kèm tên khách hàng và sản phẩm đầu tiên
    public List<Invoice> getAllInvoicesWithDetails() {
        List<Invoice> list = new ArrayList<>();
        String sql = """
            SELECT i.InvoiceID, i.CustomerID, i.UserID, i.Date, i.TotalAmount, 
                   i.Discount, i.Status, i.CreatedAt, i.UpdatedAt, i.UpdatedBy,
                   c.FullName as CustomerName,
                   p.ProductName as FirstProductName
            FROM Invoice i
            LEFT JOIN Customer c ON i.CustomerID = c.CustomerID
            LEFT JOIN (
                SELECT id1.InvoiceID, p1.ProductName
                FROM InvoiceDetail id1
                JOIN Product p1 ON id1.ProductID = p1.ProductID
                WHERE id1.InvoiceDetailID = (
                    SELECT MIN(id2.InvoiceDetailID) 
                    FROM InvoiceDetail id2 
                    WHERE id2.InvoiceID = id1.InvoiceID
                )
            ) p ON i.InvoiceID = p.InvoiceID
            ORDER BY i.Date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceID(rs.getInt("InvoiceID"));
                inv.setCustomerID(rs.getInt("CustomerID"));
                inv.setUserID(rs.getInt("UserID"));
                inv.setDate(rs.getTimestamp("Date").toLocalDateTime());
                inv.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                inv.setDiscount(rs.getBigDecimal("Discount"));
                inv.setStatus(rs.getString("Status"));

                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                if (createdAt != null) {
                    inv.setCreatedAt(createdAt.toLocalDateTime());
                }

                Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                if (updatedAt != null) {
                    inv.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                inv.setUpdatedBy(rs.getObject("UpdatedBy", Integer.class));

                // Thiết lập tên khách hàng
                String customerName = rs.getString("CustomerName");
                inv.setCustomerName(customerName != null ? customerName : "Khách vãng lai");

                // Thiết lập tên sản phẩm đầu tiên
                String firstProductName = rs.getString("FirstProductName");
                inv.setFirstProductName(firstProductName != null ? firstProductName : "N/A");

                list.add(inv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Lọc hóa đơn với tên khách hàng và sản phẩm
    public List<Invoice> filterInvoicesWithDetails(String customerFilter, String productFilter,
                                                   LocalDate fromDate, LocalDate toDate) {
        List<Invoice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT i.InvoiceID, i.CustomerID, i.UserID, i.Date, i.TotalAmount, 
                   i.Discount, i.Status, i.CreatedAt, i.UpdatedAt, i.UpdatedBy,
                   c.FullName as CustomerName,
                   p.ProductName as FirstProductName
            FROM Invoice i
            LEFT JOIN Customer c ON i.CustomerID = c.CustomerID
            LEFT JOIN InvoiceDetail id ON i.InvoiceID = id.InvoiceID
            LEFT JOIN Product p ON id.ProductID = p.ProductID
            WHERE 1=1
        """);

        List<Object> parameters = new ArrayList<>();

        if (customerFilter != null && !customerFilter.trim().isEmpty()) {
            sql.append(" AND (c.FullName LIKE ? OR c.Phone LIKE ?)");
            String customerPattern = "%" + customerFilter.trim() + "%";
            parameters.add(customerPattern);
            parameters.add(customerPattern);
        }

        if (productFilter != null && !productFilter.trim().isEmpty()) {
            sql.append(" AND p.ProductName LIKE ?");
            parameters.add("%" + productFilter.trim() + "%");
        }

        if (fromDate != null) {
            sql.append(" AND CAST(i.Date AS DATE) >= ?");
            parameters.add(Date.valueOf(fromDate));
        }

        if (toDate != null) {
            sql.append(" AND CAST(i.Date AS DATE) <= ?");
            parameters.add(Date.valueOf(toDate));
        }

        sql.append(" ORDER BY i.Date DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceID(rs.getInt("InvoiceID"));
                inv.setCustomerID(rs.getInt("CustomerID"));
                inv.setUserID(rs.getInt("UserID"));
                inv.setDate(rs.getTimestamp("Date").toLocalDateTime());
                inv.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                inv.setDiscount(rs.getBigDecimal("Discount"));
                inv.setStatus(rs.getString("Status"));

                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                if (createdAt != null) {
                    inv.setCreatedAt(createdAt.toLocalDateTime());
                }

                Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                if (updatedAt != null) {
                    inv.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                inv.setUpdatedBy(rs.getObject("UpdatedBy", Integer.class));

                // Thiết lập tên khách hàng
                String customerName = rs.getString("CustomerName");
                inv.setCustomerName(customerName != null ? customerName : "Khách vãng lai");

                // Thiết lập tên sản phẩm đầu tiên
                String firstProductName = rs.getString("FirstProductName");
                inv.setFirstProductName(firstProductName != null ? firstProductName : "N/A");

                list.add(inv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}