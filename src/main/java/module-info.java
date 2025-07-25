module Project_Sem2 {
    requires java.sql;

    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires jdk.jfr;

    opens app to javafx.fxml;
    opens controller to javafx.fxml;
   opens dao to javafx.fxml;
    opens model to javafx.fxml;

    exports app;
    exports controller;
    exports dao;
    exports model;
    exports controller.admin;
    opens controller.admin to javafx.fxml;
}
