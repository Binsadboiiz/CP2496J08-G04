package dao;

import model.ReturnPolicy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnPolicyDAO {
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public static boolean addReturnPolicy(ReturnPolicy policy) {
        String sql = "INSERT INTO ReturnPolicy (PolicyName, Description, DaysAllowed) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, policy.getPolicyName());
            stmt.setString(2, policy.getDescription());
            stmt.setInt(3, policy.getDaysAllowed());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static List<ReturnPolicy> getAllPolicies() {
        List<ReturnPolicy> policies = new ArrayList<>();
        String sql = "SELECT * FROM ReturnPolicy";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ReturnPolicy policy = new ReturnPolicy(
                        rs.getInt("ID"),
                        rs.getString("PolicyName"),
                        rs.getString("Description"),
                        rs.getInt("DaysAllowed")
                );
                policies.add(policy);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return policies;
    }
}
