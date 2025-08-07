package dao;

import model.Promotion;
import dao.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {

    public static List<Promotion> getAll() {
        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT * FROM Promotion";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Promotion p = new Promotion(
                        rs.getInt("PromotionID"),
                        rs.getString("PromotionName"),
                        rs.getString("Description"),
                        rs.getDouble("DiscountPercentage"),
                        rs.getDate("StartDate").toLocalDate(),
                        rs.getDate("EndDate").toLocalDate()
                );
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean insert(Promotion p) {
        String sql = "INSERT INTO Promotion (PromotionName, Description, DiscountPercentage, StartDate, EndDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getPromotionName());
            stmt.setString(2, p.getDescription());
            stmt.setDouble(3, p.getDiscountPercentage());
            stmt.setDate(4, Date.valueOf(p.getStartDate()));
            stmt.setDate(5, Date.valueOf(p.getEndDate()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean update(Promotion p) {
        String sql = "UPDATE Promotion SET PromotionName=?, Description=?, DiscountPercentage=?, StartDate=?, EndDate=? WHERE PromotionID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getPromotionName());
            stmt.setString(2, p.getDescription());
            stmt.setDouble(3, p.getDiscountPercentage());
            stmt.setDate(4, Date.valueOf(p.getStartDate()));
            stmt.setDate(5, Date.valueOf(p.getEndDate()));
            stmt.setInt(6, p.getPromotionID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(int id) {
        String sql = "DELETE FROM Promotion WHERE PromotionID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
