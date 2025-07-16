CREATE DATABASE CellPhoneStore;

use CellPhoneStore;
    go

CREATE TABLE [User] (
    UserID INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    Username VARCHAR(50) NOT NULL,
    Password VARCHAR(50) NOT NULL,
    Role VARCHAR(20) NOT NULL
);
INSERT INTO [User] (Username, Password, Role) VALUES ('admin', '123456', 'Admin');


CREATE TABLE Customer (
    CustomerID INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    FullName VARCHAR(100) NOT NULL,
    Phone VARCHAR(15),
    Email VARCHAR(50),
    Address VARCHAR(200)
);

CREATE TABLE Product (
    ProductID INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    ProductName VARCHAR(100) NOT NULL,
    Brand VARCHAR(50),
    Type VARCHAR(50),  -- Phone or Accessory
    Price DECIMAL NOT NULL,
    Quantity INT NOT NULL,
    Image VARCHAR(200)
);

CREATE TABLE Supplier (
    SupplierID INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    Name VARCHAR(100) NOT NULL,
    Phone VARCHAR(15),
    Address VARCHAR(200)
);

CREATE TABLE Invoice (
    InvoiceID INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    CustomerID INT,
    UserID INT,
    Date DATETIME NOT NULL,
    TotalAmount DECIMAL NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    FOREIGN KEY (UserID) REFERENCES [User](UserID)
);

CREATE TABLE InvoiceDetail (
    InvoiceDetailID INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    InvoiceID INT,
    ProductID INT,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL NOT NULL,
    FOREIGN KEY (InvoiceID) REFERENCES Invoice(InvoiceID),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

CREATE TABLE StockEntry (
    EntryID INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    SupplierID INT,
    UserID INT,
    Date DATETIME NOT NULL,
    FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID),
    FOREIGN KEY (UserID) REFERENCES [User](UserID)
);

CREATE TABLE StockEntryDetail (
    EntryDetailID INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    EntryID INT,
    ProductID INT,
    Quantity INT NOT NULL,
    UnitCost DECIMAL NOT NULL,
    FOREIGN KEY (EntryID) REFERENCES StockEntry(EntryID),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);
