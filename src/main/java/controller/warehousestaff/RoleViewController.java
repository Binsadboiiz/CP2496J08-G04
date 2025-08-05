package controller.warehousestaff;

import dao.EmployeeDAO;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Employee;
import model.User;

import java.io.File;
import java.time.LocalDate;

public class RoleViewController {

    @FXML private ImageView profileImage;
    @FXML private Label lblEmployeeName;
    @FXML private ComboBox<String> cbRole;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtPosition;
    @FXML private Button btnSaveChanges;
    @FXML private Button btnCancel;
    @FXML private Button btnChangeProfileImage;

    private Employee currentEmployee;
    private User currentUser;
    private int currentEmployeeID = 1; // Default value, should be set from outside

    @FXML
    public void initialize() {
        setupRoleComboBox();
        loadData();
    }

    private void setupRoleComboBox() {
        cbRole.getItems().addAll("Employee", "Admin", "Staff", "Cashier", "Warehouse Staff");
    }

    private void loadData() {
        try {
            // Debug: Check if EmployeeDAO returns data
            var allEmployees = EmployeeDAO.getAll();
            System.out.println("[DEBUG] Total employees found: " + allEmployees.size());

            if (allEmployees.isEmpty()) {
                // Database rỗng, hiển thị thông báo và tạo dữ liệu mẫu
                showAlert("Database không có nhân viên nào.\nTạo dữ liệu mẫu để test UI...");
                createSampleEmployee();
                return;
            }

            currentEmployee = allEmployees.stream()
                    .filter(emp -> emp.getEmployeeID() == currentEmployeeID)
                    .findFirst().orElse(null);

            // Nếu không tìm thấy employee với ID đã set, lấy employee đầu tiên
            if (currentEmployee == null && !allEmployees.isEmpty()) {
                currentEmployee = allEmployees.get(0);
                currentEmployeeID = currentEmployee.getEmployeeID();
                System.out.println("[DEBUG] Using first available employee ID: " + currentEmployeeID);
            }

            System.out.println("[DEBUG] Looking for employee ID: " + currentEmployeeID);
            System.out.println("[DEBUG] Found employee: " + (currentEmployee != null ? currentEmployee.getFullName() : "null"));

            currentUser = UserDAO.findByEmployeeID(currentEmployeeID);
            System.out.println("[DEBUG] Found user: " + (currentUser != null ? currentUser.getUsername() : "null"));

            if (currentEmployee == null) {
                showAlert("Không tìm thấy thông tin nhân viên ID: " + currentEmployeeID);
                displayEmptyForm();
                return;
            }

            if (currentUser == null) {
                showAlert("Không tìm thấy thông tin user cho nhân viên ID: " + currentEmployeeID);
                displayEmptyForm();
                return;
            }

            displayInfo();
        } catch (Exception e) {
            showAlert("Lỗi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSampleEmployee() {
        try {
            // Tạo employee mẫu
            Employee sampleEmployee = new Employee();
            sampleEmployee.setFullName("Russell Sims");
            sampleEmployee.setDateOfBirth(LocalDate.of(1990, 5, 15));
            sampleEmployee.setIdCard("123456789");
            sampleEmployee.setHometown("TP.HCM");
            sampleEmployee.setPhone("+1 555 234-5690");
            sampleEmployee.setEmail("russell@mycompany.com");
            sampleEmployee.setStatus("Active");

            int employeeId = EmployeeDAO.insertEmployee(sampleEmployee);
            if (employeeId > 0) {
                // Tạo user tương ứng
                User sampleUser = new User();
                sampleUser.setEmployeeID(employeeId);
                sampleUser.setUsername("russell.sims");
                sampleUser.setPassword("123456");
                sampleUser.setRole("Employee");

                if (UserDAO.insertUser(sampleUser)) {
                    currentEmployeeID = employeeId;
                    loadData(); // Load lại với dữ liệu mới
                } else {
                    showAlert("Lỗi tạo user mẫu!");
                }
            } else {
                showAlert("Lỗi tạo employee mẫu!");
            }
        } catch (Exception e) {
            showAlert("Lỗi tạo dữ liệu mẫu: " + e.getMessage());
        }
    }

    private void displayEmptyForm() {
        lblEmployeeName.setText("No Employee");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtPosition.setText("");
        cbRole.setValue("Employee");

        // Disable save button
        btnSaveChanges.setDisable(true);
    }

    private void displayInfo() {
        if (currentEmployee == null) {
            System.err.println("[ERROR] currentEmployee is null in displayInfo()");
            return;
        }

        if (currentUser == null) {
            System.err.println("[ERROR] currentUser is null in displayInfo()");
            return;
        }

        try {
            // Set employee name in header
            String fullName = currentEmployee.getFullName();
            if (fullName != null && !fullName.trim().isEmpty()) {
                lblEmployeeName.setText(fullName);

                // Split full name into first and last name (simple split by space)
                String[] names = fullName.trim().split(" ", 2);
                txtFirstName.setText(names.length > 0 ? names[0] : "");
                txtLastName.setText(names.length > 1 ? names[1] : "");
            } else {
                lblEmployeeName.setText("N/A");
                txtFirstName.setText("");
                txtLastName.setText("");
            }

            txtEmail.setText(currentEmployee.getEmail() != null ? currentEmployee.getEmail() : "");
            txtPhone.setText(currentEmployee.getPhone() != null ? currentEmployee.getPhone() : "");
            txtPosition.setText(getPositionFromRole(currentUser.getRole()));

            // Set role in combo box
            String roleDisplayName = getRoleDisplayName(currentUser.getRole());
            cbRole.setValue(roleDisplayName);

            // Load profile image (placeholder for now)
            loadProfileImage();

            // Enable save button
            btnSaveChanges.setDisable(false);

        } catch (Exception e) {
            System.err.println("[ERROR] Error in displayInfo(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadProfileImage() {
        // For now, set a default profile image
        // In a real application, you would load the actual image from database or file system
        try {
            // You can set a default image or load from a path
            // profileImage.setImage(new Image("path/to/default/profile.png"));
        } catch (Exception e) {
            // Handle image loading error
        }
    }

    @FXML
    private void handleChangeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(btnChangeProfileImage.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                profileImage.setImage(image);
                // Here you would also save the image path to database
            } catch (Exception e) {
                showAlert("Lỗi tải ảnh: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSaveChanges() {
        if (!validateInput()) return;

        try {
            // Update employee info
            String fullName = txtFirstName.getText().trim() + " " + txtLastName.getText().trim();
            currentEmployee.setFullName(fullName.trim());
            currentEmployee.setEmail(txtEmail.getText().trim());
            currentEmployee.setPhone(txtPhone.getText().trim());

            // Update user role
            String selectedRole = getRoleFromDisplayName(cbRole.getValue());
            currentUser.setRole(selectedRole);

            // Save to database
            boolean employeeUpdated = EmployeeDAO.updateEmployee(currentEmployee);
            boolean userUpdated = UserDAO.updateUser(currentUser);

            if (employeeUpdated && userUpdated) {
                showAlert("Cập nhật thành công!");
                loadData(); // Refresh data
            } else {
                showAlert("Lỗi cập nhật!");
            }
        } catch (Exception e) {
            showAlert("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        loadData(); // Reset to original data
    }

    private boolean validateInput() {
        if (txtFirstName.getText().trim().isEmpty()) {
            showAlert("Vui lòng nhập tên!");
            return false;
        }

        if (txtLastName.getText().trim().isEmpty()) {
            showAlert("Vui lòng nhập họ!");
            return false;
        }

        String phone = txtPhone.getText().trim();
        if (!phone.isEmpty() && !phone.matches("^[0-9+\\-\\s()]{10,15}$")) {
            showAlert("Số điện thoại không hợp lệ!");
            return false;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showAlert("Email không hợp lệ!");
            return false;
        }

        if (cbRole.getValue() == null) {
            showAlert("Vui lòng chọn vai trò!");
            return false;
        }

        return true;
    }

    private String getRoleDisplayName(String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> "Admin";
            case "staff" -> "Staff";
            case "cashier" -> "Cashier";
            case "warehousestaff" -> "Warehouse Staff";
            default -> "Employee";
        };
    }

    private String getRoleFromDisplayName(String displayName) {
        return switch (displayName) {
            case "Admin" -> "admin";
            case "Staff" -> "staff";
            case "Cashier" -> "cashier";
            case "Warehouse Staff" -> "warehousestaff";
            default -> "employee";
        };
    }

    private String getPositionFromRole(String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> "System Administrator";
            case "staff" -> "Sales Staff";
            case "cashier" -> "Cashier";
            case "warehousestaff" -> "Warehouse Staff";
            default -> "iOS Developer";
        };
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentEmployeeID(int employeeID) {
        this.currentEmployeeID = employeeID;
        loadData();
    }
}