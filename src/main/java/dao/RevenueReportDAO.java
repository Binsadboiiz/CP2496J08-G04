package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.RevenueReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RevenueReportDAO {
    private Connection connection;

    public RevenueReportDAO(Connection connection) {
        this.connection = connection;
    }

    public ObservableList<RevenueReport> getRevenueReports(String fromDate, String toDate) {
        ObservableList<RevenueReport> reports = FXCollections.observableArrayList();

        String sql = "SELECT i.Date, p.ProductName, id.UnitPrice * id.Quantity AS Amount, pay.PaymentMethod " +
                "FROM Invoice i " +
                "JOIN InvoiceDetail id ON i.InvoiceID = id.InvoiceID " +
                "JOIN Product p ON id.ProductID = p.ProductID " +
                "JOIN Payment pay ON i.InvoiceID = pay.InvoiceID " +
                "WHERE i.Date BETWEEN ? AND ?";


        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fromDate);
            stmt.setString(2, toDate);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                String product = rs.getString("product");
                double amount = rs.getDouble("amount");
                String paymentMethod = rs.getString("PaymentMethod");

                reports.add(new RevenueReport(date, product, amount, paymentMethod));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }
}
