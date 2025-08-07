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
<<<<<<< HEAD

=======
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
>>>>>>> fcc711cc46e21f7d45d0db56b46efeb653b15ea4
}
