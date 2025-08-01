package dao;

import model.SalaryHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryHistoryDAO {
    public static List<SalaryHistory> getAllSalaryHistory() {
        List<SalaryHistory> list = new ArrayList<>();
        String sql = "SELECT sh.SalaryID, e.FullName, sh.Month, sh.Year, sh.TotalSalary " +
                "FROM SalaryHistory sh JOIN Employee e ON sh.EmployeeID = e.EmployeeID";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SalaryHistory sh = new SalaryHistory(
                        rs.getInt("SalaryID"),
                        rs.getString("FullName"),
                        rs.getInt("Month"),
                        rs.getInt("Year"),
                        rs.getDouble("TotalSalary")
                );
                list.add(sh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
