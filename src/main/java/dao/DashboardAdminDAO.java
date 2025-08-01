package dao;

import java.sql.*;

import dao.DatabaseConnection;

public class DashboardAdminDAO {
    public static int getTotalProducts() {
        String sql = "SELECT COUNT(*) FROM Product";
        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {e.printStackTrace(); return 0;}
    }
    public static int getTotalEmployees() {
        String sql = "SELECT COUNT(*) FROM Employee";
        try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {e.printStackTrace(); return 0;}
    }
    /**
     * 3. Doanh thu hôm nay
     */
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
    /**
     * 4. Số mặt hàng sắp hết
     */
    public static int getStockAlerts(int threshold) {
        String sql = "SELECT COUNT(*) FROM Inventory WHERE Quantity <= ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
