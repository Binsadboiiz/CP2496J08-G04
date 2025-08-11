package controller.admin;

import dao.SupplierDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import model.Supplier;

import java.util.regex.Pattern;

public class SupplierFormController {
    @FXML private Label lblTitle;
    @FXML private TextField txtName, txtContactName, txtPhone, txtEmail, txtAddress;
    @FXML private TextArea txtNote;
    @FXML private CheckBox chkActive;

    private Supplier supplier;
    private boolean isEdit = false;

    // status để List biết hiển thị thông báo
    private boolean saved = false;
    private String lastErrorMessage = null;

    public boolean isSaved() { return saved; }
    public String getLastErrorMessage() { return lastErrorMessage; }

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\d{9,11}$");

    @FXML
    private void initialize() {
        // Chỉ cho số, max 11 ký tự
        txtPhone.setTextFormatter(new TextFormatter<String>(change -> {
            String t = change.getControlNewText();
            if (!t.matches("\\d*")) return null;
            if (t.length() > 11) return null;
            return change;
        }));
    }

    public void setModeAdd() {
        lblTitle.setText("Add Supplier");
        isEdit = false;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        isEdit = true;
        lblTitle.setText("Edit Supplier");
        txtName.setText(nvl(supplier.getName()));
        txtContactName.setText(nvl(supplier.getContactName()));
        txtPhone.setText(nvl(supplier.getPhone()));
        txtEmail.setText(nvl(supplier.getEmail()));
        txtAddress.setText(nvl(supplier.getAddress()));
        txtNote.setText(nvl(supplier.getNote()));
        chkActive.setSelected(supplier.isActive());
    }

    @FXML
    private void onSave() {
        // reset status
        saved = false;
        lastErrorMessage = null;

        String name        = trim(txtName.getText());
        String contactName = trim(txtContactName.getText());
        String phone       = trim(txtPhone.getText());
        String email       = trim(txtEmail.getText());
        String address     = trim(txtAddress.getText());
        String note        = txtNote.getText() == null ? "" : txtNote.getText().trim();

        // Required (trừ note)
        if (name.isEmpty())            { warnAndFocus("Supplier Name is required!", txtName); return; }
        if (contactName.isEmpty())     { warnAndFocus("Contact Name is required!", txtContactName); return; }
        if (phone.isEmpty())           { warnAndFocus("Phone is required!", txtPhone); return; }
        if (!PHONE_REGEX.matcher(phone).matches()) { warnAndFocus("Phone must be 9–11 digits.", txtPhone); return; }
        if (email.isEmpty())           { warnAndFocus("Email is required!", txtEmail); return; }
        if (!EMAIL_REGEX.matcher(email).matches()) { warnAndFocus("Email is invalid.", txtEmail); return; }
        if (address.isEmpty())         { warnAndFocus("Address is required!", txtAddress); return; }

        boolean ok;
        try {
            if (isEdit) {
                supplier.setName(name);
                supplier.setContactName(contactName);
                supplier.setPhone(phone);
                supplier.setEmail(email);
                supplier.setAddress(address);
                supplier.setNote(note);
                supplier.setActive(chkActive.isSelected());
                ok = SupplierDAO.update(supplier);   // DAO trả boolean
            } else {
                Supplier s = new Supplier();
                s.setName(name);
                s.setContactName(contactName);
                s.setPhone(phone);
                s.setEmail(email);
                s.setAddress(address);
                s.setNote(note);
                s.setActive(chkActive.isSelected());
                ok = SupplierDAO.insert(s);          // DAO trả boolean
            }
        } catch (Exception ex) {
            ok = false;
            lastErrorMessage = ex.getMessage();
        }

        if (ok) {
            saved = true;
            closeWindow();
        } else {
            if (lastErrorMessage == null) lastErrorMessage = "Save failed. Please try again.";
            new Alert(Alert.AlertType.ERROR, lastErrorMessage, ButtonType.OK).showAndWait();
        }
    }

    @FXML
    private void onCancel() { closeWindow(); }

    /* helpers */
    private void closeWindow() {
        ((Stage) txtName.getScene().getWindow()).close();
    }
    private static String nvl(String s) { return s == null ? "" : s; }
    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private void warnAndFocus(String msg, Control control) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
        if (control != null) {
            control.requestFocus();
            if (control instanceof TextInputControl tic) tic.selectAll();
        }
    }
}
