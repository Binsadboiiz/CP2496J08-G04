package controller.cashier;

import dao.ReturnPolicyDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ReturnPolicy;

import java.util.List;

public class ReturnPolicyController {

    @FXML private TableView<ReturnPolicy> policyTable;
    @FXML private TableColumn<ReturnPolicy, String> colPolicyName;
    @FXML private TableColumn<ReturnPolicy, String> colDescription;
    @FXML private TableColumn<ReturnPolicy, Integer> colDaysAllowed;

    public void initialize() {
        colPolicyName.setCellValueFactory(new PropertyValueFactory<>("policyName"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDaysAllowed.setCellValueFactory(new PropertyValueFactory<>("daysAllowed"));

        List<ReturnPolicy> list = ReturnPolicyDAO.getAllReturnPolicies();
        policyTable.getItems().setAll(list);
    }
}
