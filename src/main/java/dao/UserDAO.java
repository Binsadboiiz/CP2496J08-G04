package dao;

import model.User;
import java.sql.*;

public class UserDAO {
    // Insert user khi tạo Employee mới
    public static boolean insertUser(User u) {
        String sql = "INSERT INTO [User] "
                + "(Username, Password, Role, EmployeeID) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPassword());
            stmt.setString(3, u.getRole());
            stmt.setInt   (4, u.getEmployeeID());

            int rows = stmt.executeUpdate();
            System.out.println("[DEBUG] insertUser rows affected = " + rows);
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("[ERROR] insertUser failed:");
            ex.printStackTrace();
            return false;
        }
    }





    // Update role/password/email/status
    public static boolean updateUser(User u) {
        String sql = "UPDATE [User] SET Password=?, Role=? WHERE EmployeeID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getPassword());
            stmt.setString(2, u.getRole());
            stmt.setInt   (3, u.getEmployeeID());

            int rows = stmt.executeUpdate();
            System.out.println("[DEBUG] updateUser rows = " + rows);
            return rows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    // Delete user khi xóa Employee
    public static boolean deleteUserByEmployeeID(int empId) {
        String sql = "DELETE FROM [User] WHERE EmployeeID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public static User findByEmployeeID(int empId) {
        String sql = "SELECT Username, Password, Role, EmployeeID "
                + "FROM [User] WHERE EmployeeID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setEmployeeID(rs.getInt("EmployeeID"));
                    u.setUsername(rs.getString("Username"));
                    u.setPassword(rs.getString("Password"));
                    u.setRole(rs.getString("Role"));
                    // u.setEmail("");
                    // u.setStatus("");
                    return u;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
