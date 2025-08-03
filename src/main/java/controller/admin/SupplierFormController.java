package controller.admin;

import dao.SupplierDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Supplier;

public class SupplierFormController {
    @FXML private Label lblTitle;
    @FXML private TextField txtName, txtContactName, txtPhone, txtEmail, txtAddress;
    @FXML private TextArea txtNote;
    @FXML private CheckBox chkActive;

    private Supplier supplier;
    private boolean isEdit = false;

    public void setModeAdd() {
        lblTitle.setText("Add Supplier");
        isEdit = false;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        isEdit = true;
        lblTitle.setText("Edit Supplier");
        txtName.setText(supplier.getName());
        txtContactName.setText(supplier.getContactName());
        txtPhone.setText(supplier.getPhone());
        txtEmail.setText(supplier.getEmail());
        txtAddress.setText(supplier.getAddress());
        txtNote.setText(supplier.getNote());
        chkActive.setSelected(supplier.isActive());
    }

    @FXML
    private void onSave() {
        // Validate
        if (txtName.getText().isEmpty()) {
            showAlert("Supplier Name is required!");
            return;
        }
        if (isEdit) {
            supplier.setName(txtName.getText());
            supplier.setContactName(txtContactName.getText());
            supplier.setPhone(txtPhone.getText());
            supplier.setEmail(txtEmail.getText());
            supplier.setAddress(txtAddress.getText());
            supplier.setNote(txtNote.getText());
            supplier.setActive(chkActive.isSelected());
            SupplierDAO.update(supplier);
        } else {
            Supplier s = new Supplier();
            s.setName(txtName.getText());
            s.setContactName(txtContactName.getText());
            s.setPhone(txtPhone.getText());
            s.setEmail(txtEmail.getText());
            s.setAddress(txtAddress.getText());
            s.setNote(txtNote.getText());
            s.setActive(chkActive.isSelected());
            SupplierDAO.insert(s);
        }
        // Close dialog
        ((Stage) txtName.getScene().getWindow()).close();
    }

    @FXML
    private void onCancel() {
        ((Stage) txtName.getScene().getWindow()).close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
