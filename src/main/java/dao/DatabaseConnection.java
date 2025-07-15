package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String databaseName = "CellPhoneStore"; // Tên DB bạn đã tạo trong SQL Server
        String user = "sa";                     // Tài khoản bạn dùng trong ảnh
        String password = "sa";      // Thay bằng mật khẩu thật
        String serverName = "";  // Server name bạn đang dùng

        String url = "jdbc:sqlserver://" + serverName + ":1433;"
                + "databaseName=" + databaseName + ";encrypt=false;";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            databaseLink = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("❌ Kết nối thất bại: " + e.getMessage());
            e.printStackTrace();
        }

        return databaseLink;
    }
}
