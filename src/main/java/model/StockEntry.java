package model;

import java.util.Date;

public class StockEntry {
    private int entryID;
    private int supplierID;
    private int userID;
    private Date date;
    private Date createdAt;
    private Date updatedAt;
    private Integer updatedBy;

    private String supplierName;
    private String userName;

    public StockEntry() {
        //Contructor rỗng
    }

    public StockEntry(int entryID, int supplierID, int userID, Date date, Date createdAt, Date updatedAt, Integer updatedBy, String supplierName, String userName) {
        this.entryID = entryID;
        this.supplierID = supplierID;
        this.userID = userID;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.supplierName = supplierName;
        this.userName = userName;
    }

    // Getter & Setter
    public int getEntryID() {
        return entryID;
    }

    public void setEntryID(int entryID) {
        this.entryID = entryID;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
