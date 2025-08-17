package dao;

import model.StockEntry;
import dao.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockEntryDAO {
    public static List<StockEntry> getAll() {
        List<StockEntry> list = new ArrayList<>();
        String sql = """
            SELECT se.*, s.Name AS SupplierName, u.Username AS UserName
            FROM StockEntry se
            LEFT JOIN Supplier s ON se.SupplierID = s.SupplierID
            LEFT JOIN [User] u ON se.UserID = u.UserID
            ORDER BY se.Date DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                StockEntry se = new StockEntry();
                se.setEntryID(rs.getInt("EntryID"));
                se.setSupplierID(rs.getInt("SupplierID"));
                se.setUserID(rs.getInt("UserID"));
                se.setDate(rs.getTimestamp("Date"));
                se.setCreatedAt(rs.getTimestamp("CreatedAt"));
                se.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                se.setUpdatedBy((Integer)rs.getObject("UpdatedBy"));
                se.setSupplierName(rs.getString("SupplierName"));
                se.setUserName(rs.getString("UserName"));
                list.add(se);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static StockEntry getById(int entryID) {
        String sql = """
            SELECT se.*, s.Name AS SupplierName, u.Username AS UserName
            FROM StockEntry se
            LEFT JOIN Supplier s ON se.SupplierID = s.SupplierID
            LEFT JOIN [User] u ON se.UserID = u.UserID
            WHERE se.EntryID = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StockEntry se = new StockEntry();
                    se.setEntryID(rs.getInt("EntryID"));
                    se.setSupplierID(rs.getInt("SupplierID"));
                    se.setUserID(rs.getInt("UserID"));
                    se.setDate(rs.getTimestamp("Date"));
                    se.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    se.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    se.setUpdatedBy((Integer)rs.getObject("UpdatedBy"));
                    se.setSupplierName(rs.getString("SupplierName"));
                    se.setUserName(rs.getString("UserName"));
                    return se;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static int insert(StockEntry entry) {
        String sql = "INSERT INTO StockEntry (SupplierID, UserID, Date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entry.getSupplierID());
            ps.setInt(2, entry.getUserID());
            ps.setTimestamp(3, new Timestamp(entry.getDate().getTime()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }
}
