package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryDAO {

    public static int getStockAlerts(int threshold) {
        String sql = "SELECT COUNT(*) FROM Inventory WHERE Quantity <= ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean updateInventoryStock(int productID, int quantityToAdd) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            int warehouseID = ensureDefaultWarehouse(conn);

            String checkSql = "SELECT InventoryID, Quantity FROM Inventory WHERE ProductID = ? AND WarehouseID = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, productID);
                checkPs.setInt(2, warehouseID);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    String updateSql = "UPDATE Inventory SET Quantity = Quantity + ? WHERE ProductID = ? AND WarehouseID = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setInt(1, quantityToAdd);
                        updatePs.setInt(2, productID);
                        updatePs.setInt(3, warehouseID);

                        boolean success = updatePs.executeUpdate() > 0;
                        if (success) {
                            System.out.println("Updated inventory for ProductID " + productID + " by +" + quantityToAdd);
                        }
                        return success;
                    }
                } else {
                    String insertSql = "INSERT INTO Inventory (WarehouseID, ProductID, Quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, warehouseID);
                        insertPs.setInt(2, productID);
                        insertPs.setInt(3, quantityToAdd);

                        boolean success = insertPs.executeUpdate() > 0;
                        if (success) {
                            System.out.println("Created new inventory for ProductID " + productID + " with quantity " + quantityToAdd);
                        }
                        return success;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating inventory stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static int ensureDefaultWarehouse(Connection conn) throws SQLException {
        // Check if any warehouse exists
        String checkWarehouseSQL = "SELECT TOP 1 WarehouseID FROM Warehouse ORDER BY WarehouseID";
        try (PreparedStatement ps = conn.prepareStatement(checkWarehouseSQL);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int warehouseID = rs.getInt("WarehouseID");
                return warehouseID;
            }
        }

        String insertWarehouseSQL = "INSERT INTO Warehouse (WarehouseName, Address) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertWarehouseSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Main Warehouse");
            ps.setString(2, "Default Location");
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int newWarehouseID = rs.getInt(1);
                    System.out.println("Created default warehouse with ID: " + newWarehouseID);
                    return newWarehouseID;
                }
            }
        }

        throw new SQLException("Failed to create default warehouse");
    }

    public static int getCurrentStock(int productID) {
        String sql = "SELECT ISNULL(SUM(Quantity), 0) as CurrentStock FROM Inventory WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("CurrentStock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}