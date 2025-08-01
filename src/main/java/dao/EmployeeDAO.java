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

    // 1. Insert → Trả về ID sinh ra
    public static int insertEmployee(Employee emp) {
        String sql = "INSERT INTO Employee (FullName, DateOfBirth, IDCard, Hometown, Phone, Email, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, emp.getFullName());
            stmt.setDate(2, Date.valueOf(emp.getDateOfBirth()));
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

    // 2. Get All Employees
    public static List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employee ORDER BY EmployeeID DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractEmployee(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // 3. Get by ID
    public static Employee getByID(int empId) {
        String sql = "SELECT * FROM Employee WHERE EmployeeID=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractEmployee(rs);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // 4. Update Employee
    public static boolean updateEmployee(Employee emp) {
        String sql = "UPDATE Employee SET FullName=?, DateOfBirth=?, IDCard=?, "
                + "Hometown=?, Phone=?, Email=?, Status=? WHERE EmployeeID=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emp.getFullName());
            stmt.setDate(2, Date.valueOf(emp.getDateOfBirth()));
            stmt.setString(3, emp.getIdCard());
            stmt.setString(4, emp.getHometown());
            stmt.setString(5, emp.getPhone());
            stmt.setString(6, emp.getEmail());
            stmt.setString(7, emp.getStatus());
            stmt.setInt(8, emp.getEmployeeID());

            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // 5. Delete (Hard Delete)
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

    // 6. Soft Delete (Cập nhật trạng thái Inactive)
    public static boolean softDeleteEmployee(int empId) {
        String sql = "UPDATE Employee SET Status='Inactive' WHERE EmployeeID=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // 7. Search by Name (LIKE %keyword%)
    public static List<Employee> searchByName(String keyword) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employee WHERE FullName LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractEmployee(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Utility Method to Map ResultSet → Employee Object
    private static Employee extractEmployee(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeID(rs.getInt("EmployeeID"));
        e.setFullName(rs.getString("FullName"));
        e.setDateOfBirth(rs.getDate("DateOfBirth") != null ? rs.getDate("DateOfBirth").toLocalDate() : null);
        e.setIdCard(rs.getString("IDCard"));
        e.setHometown(rs.getString("Hometown"));
        e.setPhone(rs.getString("Phone"));
        e.setEmail(rs.getString("Email"));
        e.setStatus(rs.getString("Status"));
        return e;
    }

    // 8. Lấy tất cả tên nhân viên
    public static List<String> getAllEmployeeNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT FullName FROM Employee WHERE Status='Active'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                names.add(rs.getString("FullName"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return names;
    }

    // 9. Lấy EmployeeID dựa trên FullName
    public static int getEmployeeIDByName(String fullName) {
        String sql = "SELECT EmployeeID FROM Employee WHERE FullName = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("EmployeeID");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Không tìm thấy
    }
}
