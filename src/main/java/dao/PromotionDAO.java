package dao;

import model.Promotion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public static boolean addPromotion(Promotion promotion) {
        String sql = "INSERT INTO Promotion (PromotionName, Description, DiscountPercentage, StartDate, EndDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, promotion.getPromotionName());
            stmt.setString(2, promotion.getDescription());
            stmt.setDouble(3, promotion.getDiscountPercentage());
            stmt.setDate(4, Date.valueOf(promotion.getStartDate()));
            stmt.setDate(5, Date.valueOf(promotion.getEndDate()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static List<Promotion> getAllPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM Promotion";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Promotion promotion = new Promotion(
                        rs.getInt("PromotionID"),
                        rs.getString("PromotionName"),
                        rs.getString("Description"),
                        rs.getDouble("DiscountPercentage"),
                        rs.getDate("StartDate").toLocalDate(),
                        rs.getDate("EndDate").toLocalDate()
                );
                promotions.add(promotion);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return promotions;
    }
}
