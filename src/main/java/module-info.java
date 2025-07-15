module Project_Sem2 {
    requires java.sql;

    requires javafx.controls;
    requires javafx.fxml;

    opens app to javafx.fxml;
    opens controller to javafx.fxml;
//    opens dao to javafx.fxml;
    opens model to javafx.fxml;

    exports app;
    exports controller;
//    exports dao;
    exports model;
}
