module demo {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires jdk.jfr;
    requires java.desktop;

    opens app to javafx.fxml;
    opens dao to javafx.fxml;
    opens model to javafx.fxml;

    opens controller to javafx.fxml;
    opens controller.admin to javafx.fxml;
    opens controller.cashier to javafx.fxml;
    opens controller.staff to javafx.fxml;
    opens controller.warehousestaff to javafx.fxml; // ✅ THÊM DÒNG NÀY

    exports app;
    exports dao;
    exports model;
    exports controller;
    exports controller.admin;
    exports controller.cashier;
    exports controller.staff;
    exports controller.warehousestaff; // ✅ Nếu bạn cần truy cập lớp trong package này từ nơi khác (ví dụ testing, main app)
}
