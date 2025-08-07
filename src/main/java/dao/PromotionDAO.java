package dao;

import model.Promotion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {

    private final Connection conn;

    public PromotionDAO(Connection conn) {
        this.conn = conn;
    }

    // Get All Promotions from DB
    public List<Promotion> getAllPromotions() throws SQLException {
        List<Promotion> promotions = new ArrayList<>();
        String query = "SELECT PromotionID, PromotionName, AppliedProductID, DiscountPercent, StartDate, EndDate FROM Promotion";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Promotion promo = new Promotion(
                        rs.getInt("PromotionID"),
                        rs.getString("PromotionName"),
                        rs.getInt("AppliedProductID"),
                        rs.getDouble("DiscountPercent"),
                        rs.getDate("StartDate").toLocalDate(),
                        rs.getDate("EndDate").toLocalDate()
                );
                promotions.add(promo);
            }
        }
        return promotions;
    }

    // Insert New Promotion into DB
    public boolean insertPromotion(Promotion promo) {
        String query = "INSERT INTO Promotion (PromotionName, AppliedProductID, DiscountPercent, StartDate, EndDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, promo.getPromotionName());
            stmt.setInt(2, promo.getAppliedProductID());
            stmt.setDouble(3, promo.getDiscountPercent());
            stmt.setDate(4, Date.valueOf(promo.getStartDate()));
            stmt.setDate(5, Date.valueOf(promo.getEndDate()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Update an existing promotion
    public boolean updatePromotion(Promotion promo) {
        String query = "UPDATE Promotion SET PromotionName = ?, AppliedProductID = ?, DiscountPercent = ?, StartDate = ?, EndDate = ? WHERE PromotionID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, promo.getPromotionName());
            stmt.setInt(2, promo.getAppliedProductID());
            stmt.setDouble(3, promo.getDiscountPercent());
            stmt.setDate(4, Date.valueOf(promo.getStartDate()));
            stmt.setDate(5, Date.valueOf(promo.getEndDate()));
            stmt.setInt(6, promo.getPromotionID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a promotion by ID
    public boolean deletePromotion(int promotionID) {
        String query = "DELETE FROM Promotion WHERE PromotionID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, promotionID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
