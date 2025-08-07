package model;

import javafx.beans.property.*;

public class SalaryHistory {
    private IntegerProperty salaryID;
    private StringProperty employeeName;
    private IntegerProperty month;
    private IntegerProperty year;
    private DoubleProperty basicSalary;
    private IntegerProperty workingDays;
    private DoubleProperty bonus;
    private DoubleProperty penalty;
    private DoubleProperty totalSalary;

    // === CONSTRUCTOR FULL (9 PARAMS) ===
    public SalaryHistory(int salaryID, String employeeName, int month, int year, double basicSalary,
                         int workingDays, double bonus, double penalty, double totalSalary) {
        this.salaryID = new SimpleIntegerProperty(salaryID);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.month = new SimpleIntegerProperty(month);
        this.year = new SimpleIntegerProperty(year);
        this.basicSalary = new SimpleDoubleProperty(basicSalary);
        this.workingDays = new SimpleIntegerProperty(workingDays);
        this.bonus = new SimpleDoubleProperty(bonus);
        this.penalty = new SimpleDoubleProperty(penalty);
        this.totalSalary = new SimpleDoubleProperty(totalSalary);
    }

    // === GETTERS & SETTERS ===
    public int getSalaryID() { return salaryID.get(); }
    public void setSalaryID(int salaryID) { this.salaryID.set(salaryID); }
    public IntegerProperty salaryIDProperty() { return salaryID; }

    public String getEmployeeName() { return employeeName.get(); }
    public void setEmployeeName(String employeeName) { this.employeeName.set(employeeName); }
    public StringProperty employeeNameProperty() { return employeeName; }

    public int getMonth() { return month.get(); }
    public void setMonth(int month) { this.month.set(month); }
    public IntegerProperty monthProperty() { return month; }

    public int getYear() { return year.get(); }
    public void setYear(int year) { this.year.set(year); }
    public IntegerProperty yearProperty() { return year; }

    public double getBasicSalary() { return basicSalary.get(); }
    public void setBasicSalary(double basicSalary) { this.basicSalary.set(basicSalary); }
    public DoubleProperty basicSalaryProperty() { return basicSalary; }

    public int getWorkingDays() { return workingDays.get(); }
    public void setWorkingDays(int workingDays) { this.workingDays.set(workingDays); }
    public IntegerProperty workingDaysProperty() { return workingDays; }

    public double getBonus() { return bonus.get(); }
    public void setBonus(double bonus) { this.bonus.set(bonus); }
    public DoubleProperty bonusProperty() { return bonus; }

    public double getPenalty() { return penalty.get(); }
    public void setPenalty(double penalty) { this.penalty.set(penalty); }
    public DoubleProperty penaltyProperty() { return penalty; }

    public double getTotalSalary() { return totalSalary.get(); }
    public void setTotalSalary(double totalSalary) { this.totalSalary.set(totalSalary); }
    public DoubleProperty totalSalaryProperty() { return totalSalary; }

    @Override
    public String toString() {
        return employeeName.get();
    }
}
