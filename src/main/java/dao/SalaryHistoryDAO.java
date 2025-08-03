package dao;

import model.SalaryHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryHistoryDAO {
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public static boolean addSalaryHistoryDAO(SalaryHistory salary) {
        String sql = "INSERT INTO Salary (EmployeeID, Amount, Date) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salary.getEmployeeId());
            stmt.setDouble(2, salary.getAmount());
            stmt.setDate(3, Date.valueOf(salary.getDate()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static List<SalaryHistory> getAllSalaries() {
        List<SalaryHistory> salaries = new ArrayList<>();
        String sql = "SELECT * FROM Salary";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SalaryHistory salary = new SalaryHistory(
                        rs.getInt("EmployeeID"),
                        rs.getDouble("Amount"),
                        rs.getDate("Date").toLocalDate()
                );
                salaries.add(salary);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return salaries;
    }
}
