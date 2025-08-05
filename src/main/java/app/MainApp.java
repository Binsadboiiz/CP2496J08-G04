package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.sql.Connection;
import dao.DatabaseConnection;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
<<<<<<< HEAD
        Parent root = FXMLLoader.load(getClass().getResource("/view/warehousestaff/WarehouseStaffDashboard.fxml"));
=======
        Parent root = FXMLLoader.load(getClass().getResource("/view/staff/SceneStaff.fxml"));
>>>>>>> 48623bdbe06c1d41030e78be161a777827088e4f
        primaryStage.getIcons().add(
                new Image(getClass().getResourceAsStream("/images/logo-app.png"))
        );
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("SQLServer Connected Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }
}
