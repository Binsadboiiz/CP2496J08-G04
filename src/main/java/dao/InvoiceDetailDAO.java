package dao;

import model.InvoiceDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InvoiceDetailDAO {

    public void insertInvoiceDetail(InvoiceDetail detail) {
        String sql = "INSERT INTO InvoiceDetail (InvoiceID, ProductID, Quantity, UnitPrice, Discount) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detail.getInvoiceID());
            stmt.setInt(2, detail.getProductID());
            stmt.setInt(3, detail.getQuantity());
            stmt.setBigDecimal(4, detail.getUnitPrice());
            stmt.setBigDecimal(5, detail.getDiscount());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
