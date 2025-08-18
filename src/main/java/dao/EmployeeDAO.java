package dao;

import model.Employee;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // Lấy connection chung
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // ===== SQL hằng số =====
    private static final String SQL_GET_ALL = """
        SELECT EmployeeID, FullName, DateOfBirth, IDCard, Hometown, Phone, Email, Status
        FROM Employee
        ORDER BY EmployeeID DESC
    """;

    private static final String SQL_INSERT = """
        INSERT INTO Employee (FullName, DateOfBirth, IDCard, Hometown, Phone, Email, Status)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    private static final String SQL_UPDATE = """
        UPDATE Employee
           SET FullName = ?, DateOfBirth = ?, IDCard = ?, Hometown = ?, Phone = ?, Email = ?, Status = ?
         WHERE EmployeeID = ?
    """;

    private static final String SQL_DELETE = """
        DELETE FROM Employee WHERE EmployeeID = ?
    """;

    // ĐỔI TÊN BẢNG / CỘT Ở ĐÂY NẾU KHÁC
    private static final String SQL_HAS_INVOICES = """
        SELECT TOP 1 1 FROM Invoice WHERE EmployeeID = ?
    """;

    // 1) Insert → trả về ID sinh ra, -1 nếu lỗi
    public static int insertEmployee(Employee emp) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, emp.getFullName());
            stmt.setDate  (2, emp.getDateOfBirth() != null ? Date.valueOf(emp.getDateOfBirth()) : null);
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

    // 2) Get all
    public static List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee e = new Employee();
                e.setEmployeeID(rs.getInt("EmployeeID"));
                e.setFullName(rs.getString("FullName"));

                Date dob = rs.getDate("DateOfBirth");
                e.setDateOfBirth(dob != null ? dob.toLocalDate() : null);

                e.setIdCard (rs.getString("IDCard"));
                e.setHometown(rs.getString("Hometown"));
                e.setPhone  (rs.getString("Phone"));
                e.setEmail  (rs.getString("Email"));
                e.setStatus (rs.getString("Status"));
                list.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // 3) Update
    public static boolean updateEmployee(Employee emp) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, emp.getFullName());
            stmt.setDate  (2, emp.getDateOfBirth() != null ? Date.valueOf(emp.getDateOfBirth()) : null);
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

    // 4) Delete (HARD delete) — có check hóa đơn để chặn
    public static boolean deleteEmployee(int empId) {
        if (hasInvoices(empId)) {
            // controller sẽ show cảnh báo trước, nên ở đây chỉ trả false cho chắc
            return false;
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, empId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // có thể do FK/trigger chặn
        }
    }

    // Check nhân viên đã có hóa đơn chưa (không throws) — an toàn: lỗi DB coi như có hóa đơn
    public static boolean hasInvoices(int employeeId) {
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_HAS_INVOICES)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return true;
        }
    }

    // Tổng số nhân viên
    public static int getTotalEmployees() {
        String sql = "SELECT COUNT(*) FROM Employee";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Lấy danh sách tên (FullName)
    public static List<String> getAllEmployeeNames() {
        List<String> employeeNames = new ArrayList<>();
        String sql = "SELECT FullName FROM Employee ORDER BY FullName";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employeeNames.add(rs.getString("FullName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeNames;
    }

    // Lấy EmployeeID từ FullName
    public static int getEmployeeIDByName(String fullName) {
        String sql = "SELECT EmployeeID FROM Employee WHERE FullName = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("EmployeeID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Không tìm thấy
    }

    // Lấy FullName từ EmployeeID
    public static String getEmployeeNameByID(int employeeID) {
        String sql = "SELECT FullName FROM Employee WHERE EmployeeID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("FullName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
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
