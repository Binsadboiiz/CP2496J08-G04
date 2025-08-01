package controller.cashier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PromotionManagementController {

    @FXML private TableView<Promotion> tblPromotions;
    @FXML private TableColumn<Promotion, String> colName;
    @FXML private TableColumn<Promotion, String> colDescription;
    @FXML private TableColumn<Promotion, String> colStartDate;
    @FXML private TableColumn<Promotion, String> colEndDate;
    @FXML private TableColumn<Promotion, String> colStatus;

    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    @FXML private TextField txtStartDate;
    @FXML private TextField txtEndDate;
    @FXML private TextField txtStatus;

    private ObservableList<Promotion> promotions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDummyData();
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadDummyData() {
        promotions.addAll(
                new Promotion("Summer promotion", "20% off all products", "01/06/2023", "30/06/2025", "took place"),
                new Promotion("Buy 1 Get 1 Free", "Buy any 1 product, get 1 free of the same type", "01/07/2025", "15/07/2025", "took place"),
                new Promotion("Birthday discount", "15% discount for customers whose birthday is this month", "01/07/2025", "31/07/2025", "Ongoing"),
                new Promotion("Year-end promotion", "30% off all products for the end of year holidays", "15/12/2025", "31/12/2025", "Coming soon")
        );
        tblPromotions.setItems(promotions);
    }

    @FXML
    private void addPromotion() {
        String name = txtName.getText();
        String description = txtDescription.getText();
        String startDate = txtStartDate.getText();
        String endDate = txtEndDate.getText();
        String status = txtStatus.getText();

        if (!name.isEmpty() && !description.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty() && !status.isEmpty()) {
            Promotion newPromotion = new Promotion(name, description, startDate, endDate, status);
            promotions.add(newPromotion);

            txtName.clear();
            txtDescription.clear();
            txtStartDate.clear();
            txtEndDate.clear();
            txtStatus.clear();

            System.out.println("Promotion added: " + name);
        } else {
            System.out.println("Please fill in all information.");
        }
    }

    @FXML
    private void deletePromotion() {
        Promotion selectedPromotion = tblPromotions.getSelectionModel().getSelectedItem();
        if (selectedPromotion != null) {
            promotions.remove(selectedPromotion);
            System.out.println("Promotion removed: " + selectedPromotion.getName());
        } else {
            System.out.println("Please select a promotion to delete.");
        }
    }

    public static class Promotion {
        private final String name, description, startDate, endDate, status;

        public Promotion(String name, String description, String startDate, String endDate, String status) {
            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getStatus() { return status; }
    }
}
