package controller.cashier;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.NumberFormat;
import java.util.Locale;

public class CalculatorSalaryController {

    @FXML private ComboBox<String> cbEmployee;
    @FXML private ComboBox<String> cbMonth;
    @FXML private TextField txtBasicSalary;
    @FXML private Spinner<Integer> spnWorkingDays;
    @FXML private TextField txtBonus;
    @FXML private TextField txtPenalty;
    @FXML private Label lblResultDetails;  // ← Thêm Label hiển thị kết quả chi tiết
    @FXML private Label lblTotalSalary;

    @FXML
    public void initialize() {
        cbEmployee.getItems().addAll("Nguyen Van A", "Tran Thi B", "Le Van C");
        cbEmployee.setEditable(true);

        cbMonth.getItems().addAll(
                "1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12"
        );
        cbMonth.setEditable(true);

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 31, 0);
        spnWorkingDays.setValueFactory(valueFactory);
        spnWorkingDays.getValueFactory().setValue(0);
        spnWorkingDays.setEditable(true);

        setupNumericTextField(txtBasicSalary);
        setupNumericTextField(txtBonus);
        setupNumericTextField(txtPenalty);
    }


    private void setupNumericTextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue);
            }
        });
    }

    @FXML
    private void calculateSalary() {
        try {
            // Kiểm tra dữ liệu bắt buộc
            if (cbEmployee.getValue() == null || cbEmployee.getValue().trim().isEmpty()) {
                lblTotalSalary.setText("Please enter Employee Name.");
                lblTotalSalary.setStyle("-fx-text-fill: red;");
                return;
            }
            if (cbMonth.getValue() == null || cbMonth.getValue().trim().isEmpty()) {
                lblTotalSalary.setText("Please select Month.");
                lblTotalSalary.setStyle("-fx-text-fill: red;");
                return;
            }
            if (txtBasicSalary.getText().isEmpty()) {
                lblTotalSalary.setText("Please enter Basic Salary.");
                lblTotalSalary.setStyle("-fx-text-fill: red;");
                return;
            }

            double basicSalary = Double.parseDouble(txtBasicSalary.getText());
            int workingDays = spnWorkingDays.getValue() != null ? spnWorkingDays.getValue() : 0;
            double bonus = txtBonus.getText().isEmpty() ? 0 : Double.parseDouble(txtBonus.getText());
            double penalty = txtPenalty.getText().isEmpty() ? 0 : Double.parseDouble(txtPenalty.getText());

            // Tính toán lương
            double dailySalary = basicSalary / 26;
            double totalSalary = (dailySalary * workingDays) + bonus - penalty;

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedSalary = currencyFormat.format(totalSalary);

            // Hiển thị kết quả chi tiết
            lblResultDetails.setText("Staff: " + cbEmployee.getValue() +
                    "\nMonth: " + cbMonth.getValue() +
                    "\nWork day: " + workingDays);
            lblResultDetails.setStyle("-fx-text-fill: #333333;");

            lblTotalSalary.setText("Total salary: " + formattedSalary);
            lblTotalSalary.setStyle("-fx-text-fill: green;");

        } catch (NumberFormatException e) {
            lblTotalSalary.setText("Please enter correct number format.");
            lblTotalSalary.setStyle("-fx-text-fill: red;");
        }
    }
}
