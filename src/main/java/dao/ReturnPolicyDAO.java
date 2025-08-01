package dao;

import model.ReturnPolicy;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnPolicyDAO {
    public static List<ReturnPolicy> getAllReturnPolicies() {
        List<ReturnPolicy> list = new ArrayList<>();
        String sql = "SELECT PolicyID, PolicyName, Description, DaysAllowed FROM ReturnPolicy";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ReturnPolicy rp = new ReturnPolicy(
                        rs.getInt("PolicyID"),
                        rs.getString("PolicyName"),
                        rs.getString("Description"),
                        rs.getInt("DaysAllowed")
                );
                list.add(rp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
