package dao;

import model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public static List<Transaction> getRecentTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(1001, "Nguyễn Văn A", "2025-07-28 10:00", 500000, "Completed"));
        transactions.add(new Transaction(1002, "Trần Thị B", "2025-07-28 11:30", 750000, "Completed"));
        transactions.add(new Transaction(1003, "Lê Văn C", "2025-07-28 12:45", 250000, "Pending"));
        return transactions;
    }

    public static double getTodayRevenue() {
        // Dummy data: Tổng doanh thu hôm nay
        return 1500000; // 1,500,000 VND
    }

    public static int getTodayTransactionCount() {
        // Dummy data: Số lượng giao dịch hôm nay
        return 3;
    }
}
