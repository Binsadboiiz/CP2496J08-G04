package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlserver://cellphonestore-sql.database.windows.net:1433;"
            + "database=CellPhoneStore;"
            + "user=sqladmin@cellphonestore-sql;"
            + "password=CellphoneS1@;"
            + "encrypt=true;"
            + "trustServerCertificate=false;"
            + "hostNameInCertificate=*.database.windows.net;"
            + "loginTimeout=30;";

    private static final String USER = "sqladmin@cellphonestore-sql";
    private static final String PASS = "CellphoneS1@";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}