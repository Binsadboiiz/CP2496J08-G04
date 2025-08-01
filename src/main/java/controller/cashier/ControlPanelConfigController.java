package controller.cashier;

import dao.ControlPanelConfigDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ControlPanelConfig;

import java.util.List;

public class ControlPanelConfigController {

    @FXML private TableView<ControlPanelConfig> configTable;
    @FXML private TableColumn<ControlPanelConfig, String> colConfigName;
    @FXML private TableColumn<ControlPanelConfig, String> colConfigValue;

    public void initialize() {
        colConfigName.setCellValueFactory(new PropertyValueFactory<>("configName"));
        colConfigValue.setCellValueFactory(new PropertyValueFactory<>("configValue"));

        List<ControlPanelConfig> list = ControlPanelConfigDAO.getAllConfigs();
        configTable.getItems().setAll(list);
    }
}
