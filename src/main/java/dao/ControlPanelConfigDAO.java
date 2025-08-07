package dao;

import model.ControlPanelConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanelConfigDAO {
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public static List<ControlPanelConfig> getAllConfigs() {
        List<ControlPanelConfig> list = new ArrayList<>();
        String sql = "SELECT * FROM ControlPanelConfig";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ControlPanelConfig config = new ControlPanelConfig(
                        rs.getInt("ConfigID"),
                        rs.getString("ConfigName"),
                        rs.getString("ConfigValue")
                );
                list.add(config);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean updateConfig(ControlPanelConfig config) {
        String sql = "UPDATE ControlPanelConfig SET ConfigValue = ? WHERE ConfigID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, config.getConfigValue());
            ps.setInt(2, config.getConfigID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // <-- xử lý lỗi tại đây
        }
        return false;
    }
}
