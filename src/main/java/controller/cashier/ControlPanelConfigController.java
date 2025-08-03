package controller.cashier;

import dao.ControlPanelConfigDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ControlPanelConfig;

public class ControlPanelConfigController {
    @FXML private TableView<ControlPanelConfig> configTable;
    @FXML private TableColumn<ControlPanelConfig, Integer> idColumn;
    @FXML private TableColumn<ControlPanelConfig, String> nameColumn;
    @FXML private TableColumn<ControlPanelConfig, String> valueColumn;

    @FXML private TextField valueField;

    private ObservableList<ControlPanelConfig> configList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getConfigID()).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getConfigName()));
        valueColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getConfigValue()));

        loadConfigs();
    }

    private void loadConfigs() {
        configList.clear();
        configList.addAll(ControlPanelConfigDAO.getAllConfigs());
        configTable.setItems(configList);
    }

    @FXML
    public void handleUpdateConfig() {
        ControlPanelConfig selectedConfig = configTable.getSelectionModel().getSelectedItem();
        if (selectedConfig != null) {
            selectedConfig.setConfigValue(valueField.getText());
            if (ControlPanelConfigDAO.updateConfig(selectedConfig)) {
                loadConfigs();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Configuration updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update configuration.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a configuration to update.");
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
