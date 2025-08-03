package controller.cashier;

import dao.PromotionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Promotion;

import java.time.LocalDate;

public class PromotionManagementController {
    @FXML private TableView<Promotion> promotionTable;
    @FXML private TableColumn<Promotion, Integer> idColumn;
    @FXML private TableColumn<Promotion, String> nameColumn;
    @FXML private TableColumn<Promotion, String> descriptionColumn;
    @FXML private TableColumn<Promotion, Double> discountColumn;
    @FXML private TableColumn<Promotion, LocalDate> startDateColumn;
    @FXML private TableColumn<Promotion, LocalDate> endDateColumn;

    @FXML private TextField promotionNameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField discountField;
    @FXML private DatePicker startDateField;
    @FXML private DatePicker endDateField;

    private ObservableList<Promotion> promotionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPromotionID()).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPromotionName()));
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        discountColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getDiscountPercentage()).asObject());
        startDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStartDate()));
        endDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEndDate()));

        loadPromotions();
    }

    private void loadPromotions() {
        promotionList.clear();
        promotionList.addAll(PromotionDAO.getAllPromotions());
        promotionTable.setItems(promotionList);
    }

    @FXML
    public void handleAddPromotion() {
        String name = promotionNameField.getText();
        String description = descriptionField.getText();
        double discount = Double.parseDouble(discountField.getText());
        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();

        Promotion promotion = new Promotion(0, name, description, discount, startDate, endDate);
        if (PromotionDAO.addPromotion(promotion)) {
            loadPromotions();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Promotion added successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add promotion.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
