package controller;

import model.ITRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class ITRequestController {
    @FXML private TableView<ITRequest> tableRequests;
    @FXML private TableColumn<ITRequest, Number> colID;
    @FXML private TableColumn<ITRequest, String> colName, colEmail, colType, colDetails;
    @FXML private TableColumn<ITRequest, LocalDate> colDate;
    @FXML private TextField txtName, txtEmail, txtType, txtDetails;

    private ObservableList<ITRequest> requestList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colID.setCellValueFactory(data -> data.getValue().reqIDProperty());
        colName.setCellValueFactory(data -> data.getValue().reqNameProperty());
        colDate.setCellValueFactory(data -> data.getValue().reqDateProperty());
        colEmail.setCellValueFactory(data -> data.getValue().reqEmailProperty());
        colType.setCellValueFactory(data -> data.getValue().reqTypeProperty());
        colDetails.setCellValueFactory(data -> data.getValue().reqDetailsProperty());

        tableRequests.setItems(requestList);
    }

    @FXML
    public void addRequest() {
        int id = requestList.size() + 1;
        String name = txtName.getText();
        String email = txtEmail.getText();
        String type = txtType.getText();
        String details = txtDetails.getText();
        LocalDate date = LocalDate.now();

        ITRequest req = new ITRequest(id, name, date, email, type, details);
        requestList.add(req);

        clearFields();
    }

    @FXML
    public void updateRequest() {
        ITRequest selected = tableRequests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.reqDetailsProperty().set(txtDetails.getText());
            tableRequests.refresh();
        }
    }

    @FXML
    public void deleteRequest() {
        ITRequest selected = tableRequests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            requestList.remove(selected);
        }
    }

    @FXML
    public void refreshTable() {
        tableRequests.refresh();
    }

    private void clearFields() {
        txtName.clear();
        txtEmail.clear();
        txtType.clear();
        txtDetails.clear();
    }
}
