package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InvoiceDAO {
    public static double getTodaysSales() {
        String sql = ""
                + "SELECT ISNULL(SUM(TotalAmount), 0) "
                + "FROM Invoice "
                + "WHERE CONVERT(date, Date) = CONVERT(date, GETDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
