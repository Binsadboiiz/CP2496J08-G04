package app;

import dao.ProductDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.sql.Connection;
import dao.DatabaseConnection;
import javafx.stage.StageStyle;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
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
            ProductDAO productDAO = new ProductDAO(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }
}
