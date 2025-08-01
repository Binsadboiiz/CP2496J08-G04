package dao;

import model.ControlPanelConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanelConfigDAO {
    public static List<ControlPanelConfig> getAllConfigs() {
        List<ControlPanelConfig> list = new ArrayList<>();
        String sql = "SELECT ConfigID, ConfigName, ConfigValue FROM ControlPanelConfig";
        try (Connection conn = DatabaseConnection.getConnection();
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
}
