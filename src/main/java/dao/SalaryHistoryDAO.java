package dao;

import model.SalaryHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryHistoryDAO {
    private Connection conn;

    public SalaryHistoryDAO(Connection conn) {
        this.conn = conn;
    }

    // 1. Get All SalaryHistories (FULL CỘT)
    public List<SalaryHistory> getAllSalaryHistories() {
        List<SalaryHistory> list = new ArrayList<>();
        String sql = """
            SELECT sh.HistoryID, e.EmployeeName, sh.Month, sh.Year, sh.BasicSalary,
                   sh.WorkingDays, sh.Bonus, sh.Penalty, sh.TotalSalary
            FROM SalaryHistory sh
            JOIN Employee e ON sh.EmployeeID = e.EmployeeID
            """;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SalaryHistory history = new SalaryHistory(
                        rs.getInt("HistoryID"),
                        rs.getString("EmployeeName"),
                        rs.getInt("Month"),
                        rs.getInt("Year"),
                        rs.getDouble("BasicSalary"),
                        rs.getInt("WorkingDays"),
                        rs.getDouble("Bonus"),
                        rs.getDouble("Penalty"),
                        rs.getDouble("TotalSalary")
                );
                list.add(history);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Insert SalaryHistory (Full Data)
    public boolean insertSalaryHistory(SalaryHistory history, int employeeID) {
        String sql = """
            INSERT INTO SalaryHistory (EmployeeID, Month, Year, BasicSalary, WorkingDays, Bonus, Penalty, TotalSalary)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeID);
            ps.setInt(2, history.getMonth());
            ps.setInt(3, history.getYear());
            ps.setDouble(4, history.getBasicSalary());
            ps.setInt(5, history.getWorkingDays());
            ps.setDouble(6, history.getBonus());
            ps.setDouble(7, history.getPenalty());
            ps.setDouble(8, history.getTotalSalary());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Get EmployeeID by EmployeeName
    public int getEmployeeIDByName(String employeeName) {
        String sql = "SELECT EmployeeID FROM Employee WHERE EmployeeName = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("EmployeeID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }
    public List<String> getAllEmployeeNames() throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT FullName FROM Employee";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("FullName"));
            }
        }

        return names;
    }

}
