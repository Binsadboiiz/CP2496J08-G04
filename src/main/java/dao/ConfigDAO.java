//package dao;
//
//import model.ControlPanelConfig;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ConfigDAO {
//    private final Connection conn;
//
//    public ConfigDAO(Connection conn) {
//        this.conn = conn;
//    }
//
//    public List<ControlPanelConfig> getAllConfigs() {
//        List<ControlPanelConfig> list = new ArrayList<>();
//        String sql = "SELECT * FROM Config";
//
//        try (PreparedStatement stmt = conn.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//            while (rs.next()) {
//                int id = rs.getInt("ConfigID");
//                String name = rs.getString("ConfigName");
//                String value = rs.getString("ConfigValue");
//
//                list.add(new ControlPanelConfig(id, name, value));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    public boolean updateConfigValue(int configID, String newValue) {
//        String sql = "UPDATE Config SET ConfigValue = ? WHERE ConfigID = ?";
//
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, newValue);
//            stmt.setInt(2, configID);
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//}
