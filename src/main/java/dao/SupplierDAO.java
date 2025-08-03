package dao;

import model.Supplier;
import dao.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    // Lấy tất cả supplier đang active
    public static List<Supplier> getAll() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Supplier WHERE IsActive = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Supplier s = mapResultSet(rs);
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy theo ID
    public static Supplier getById(int supplierID) {
        String sql = "SELECT * FROM Supplier WHERE SupplierID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm supplier mới
    public static boolean insert(Supplier s) {
        String sql = "INSERT INTO Supplier (Name, ContactName, Phone, Email, Address, Note, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getContactName());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getAddress());
            ps.setString(6, s.getNote());
            ps.setBoolean(7, s.isActive());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sửa supplier
    public static boolean update(Supplier s) {
        String sql = "UPDATE Supplier SET Name=?, ContactName=?, Phone=?, Email=?, Address=?, Note=?, IsActive=? WHERE SupplierID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getContactName());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getAddress());
            ps.setString(6, s.getNote());
            ps.setBoolean(7, s.isActive());
            ps.setInt(8, s.getSupplierID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm (soft delete)
    public static boolean softDelete(int supplierID) {
        String sql = "UPDATE Supplier SET IsActive = 0 WHERE SupplierID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Map dữ liệu từ ResultSet sang Supplier
    private static Supplier mapResultSet(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setSupplierID(rs.getInt("SupplierID"));
        s.setName(rs.getString("Name"));
        s.setContactName(rs.getString("ContactName"));
        s.setPhone(rs.getString("Phone"));
        s.setEmail(rs.getString("Email"));
        s.setAddress(rs.getString("Address"));
        s.setNote(rs.getString("Note"));
        s.setCreatedDate(rs.getString("CreatedDate"));
        s.setActive(rs.getBoolean("IsActive"));
        return s;
    }
}
