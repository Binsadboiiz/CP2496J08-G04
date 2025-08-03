package dao;

import model.Transaction;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    public static List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT i.InvoiceID, c.CustomerName, i.Date, SUM(id.Quantity * id.UnitPrice) AS TotalAmount " +
                "FROM Invoice i " +
                "JOIN Customer c ON i.CustomerID = c.CustomerID " +
                "JOIN InvoiceDetail id ON i.InvoiceID = id.InvoiceID " +
                "GROUP BY i.InvoiceID, c.CustomerName, i.Date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("InvoiceID"),
                        rs.getString("CustomerName"),
                        rs.getDate("Date").toLocalDate(),
                        rs.getDouble("TotalAmount")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
