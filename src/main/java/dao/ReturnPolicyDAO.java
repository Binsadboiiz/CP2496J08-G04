package dao;

import model.ReturnPolicy;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnPolicyDAO {
    private final Connection conn;

    public ReturnPolicyDAO(Connection conn) {
        this.conn = conn;
    }

    public List<ReturnPolicy> getAllPolicies() {
        List<ReturnPolicy> list = new ArrayList<>();
        String sql = "SELECT * FROM ReturnPolicy";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ReturnPolicy policy = new ReturnPolicy(
                        rs.getInt("PolicyID"),
                        rs.getString("PolicyName"),
                        rs.getString("Description"),
                        rs.getInt("DaysAllowed")
                );
                list.add(policy);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // <== Xử lý lỗi ngay tại DAO
        }
        return list;
    }

    public boolean insertPolicy(ReturnPolicy policy) {
        String sql = "INSERT INTO ReturnPolicy (PolicyName, Description, DaysAllowed) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, policy.getPolicyName());
            stmt.setString(2, policy.getDescription());
            stmt.setInt(3, policy.getDaysAllowed());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updatePolicy(ReturnPolicy policy) {
        String sql = "UPDATE ReturnPolicy SET PolicyName = ?, Description = ?, DaysAllowed = ? WHERE PolicyID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, policy.getPolicyName());
            ps.setString(2, policy.getDescription());
            ps.setInt(3, policy.getDaysAllowed());
            ps.setInt(4, policy.getPolicyID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePolicy(int policyID) {
        String sql = "DELETE FROM ReturnPolicy WHERE PolicyID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, policyID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insertReturnPolicy(ReturnPolicy policy) {
        String sql = "INSERT INTO ReturnPolicy (PolicyName, Description, DaysAllowed) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, policy.getPolicyName());
            ps.setString(2, policy.getDescription());
            ps.setInt(3, policy.getDaysAllowed());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();  // <== Bắt lỗi ở đây luôn
            return false;
        }
    }

}
