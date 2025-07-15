package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffController implements Initializable {

    @FXML
    private GridPane productGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Demo 6 sản phẩm
        for (int i = 0; i < 6; i++) {
            VBox productCard = createProductCard("Sản phẩm " + (i + 1), "100.000đ", "10");
            productGrid.add(productCard, i % 3, i / 3);
        }
    }

    private VBox createProductCard(String name, String price, String quantity) {
        VBox card = new VBox(5);
        card.getStyleClass().add("product-card");

        Label image = new Label("Hình sản phẩm");
        Label lblName = new Label(name);
        Label lblPrice = new Label(price);
        Label lblQuantity = new Label("SL: " + quantity);

        card.getChildren().addAll(image, lblName, lblPrice, lblQuantity);
        return card;
    }
}
