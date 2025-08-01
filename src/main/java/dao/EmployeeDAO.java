package dao;

import model.Employee;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    // Kết nối chung
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // 1. Insert → trả về ID sinh ra
    public static int insertEmployee(Employee emp) {
        String sql = "INSERT INTO Employee "
                + "(FullName, DateOfBirth, IDCard, Hometown, Phone, Email, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, emp.getFullName());
            stmt.setDate  (2, Date.valueOf(emp.getDateOfBirth()));
            stmt.setString(3, emp.getIdCard());
            stmt.setString(4, emp.getHometown());
            stmt.setString(5, emp.getPhone());
            stmt.setString(6, emp.getEmail());
            stmt.setString(7, emp.getStatus());

            if (stmt.executeUpdate() == 0) return -1;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    // 2. Get all
    public static List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employee";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee e = new Employee();
                e.setEmployeeID(rs.getInt("EmployeeID"));
                e.setFullName   (rs.getString("FullName"));
                e.setDateOfBirth(rs.getDate("DateOfBirth").toLocalDate());
                e.setIdCard     (rs.getString("IDCard"));
                e.setHometown   (rs.getString("Hometown"));
                e.setPhone      (rs.getString("Phone"));
                e.setEmail      (rs.getString("Email"));
                e.setStatus     (rs.getString("Status"));
                list.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // 3. Update
    public static boolean updateEmployee(Employee emp) {
        String sql = "UPDATE Employee SET FullName=?, DateOfBirth=?, IDCard=?, "
                + "Hometown=?, Phone=?, Email=?, Status=? WHERE EmployeeID=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emp.getFullName());
            stmt.setDate  (2, Date.valueOf(emp.getDateOfBirth()));
            stmt.setString(3, emp.getIdCard());
            stmt.setString(4, emp.getHometown());
            stmt.setString(5, emp.getPhone());
            stmt.setString(6, emp.getEmail());
            stmt.setString(7, emp.getStatus());
            stmt.setInt   (8, emp.getEmployeeID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // 4. Delete
    public static boolean deleteEmployee(int empId) {
        String sql = "DELETE FROM Employee WHERE EmployeeID=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public static int getTotalEmployees() {
        String sql = "SELECT COUNT(*) FROM Employee";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {e.printStackTrace(); return 0;}
    }
}
