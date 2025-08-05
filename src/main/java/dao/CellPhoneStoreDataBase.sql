CREATE DATABASE CellPhoneStore;
USE CellPhoneStore;
GO

CREATE TABLE [User]
(
    UserID   INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    Username VARCHAR(50)                   UNIQUE,
    Password VARCHAR(50)                    NOT NULL,
    Role VARCHAR(50) NOT NULL
);

CREATE TABLE Customer
(
    CustomerID    INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    FullName      VARCHAR(100)                   NOT NULL,
    Phone         VARCHAR(15),
    Email         VARCHAR(50),
    Address       VARCHAR(200),
    LoyaltyPoints INT DEFAULT 0
);

CREATE TABLE Warehouse
(
    WarehouseID   INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    WarehouseName VARCHAR(100)                   NOT NULL,
    Address       VARCHAR(200)
);

CREATE TABLE Product
(
    ProductID   INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    ProductName VARCHAR(100)                   NOT NULL,
    ProductCode VARCHAR(50) UNIQUE,
    Brand       VARCHAR(50),
    Type        VARCHAR(50),
    Price       DECIMAL                        NOT NULL,
    Description TEXT,
    Image       VARCHAR(200),
    CreatedAt   DATETIME DEFAULT GETDATE(),
    UpdatedAt   DATETIME,
    CONSTRAINT CHK_Price CHECK (Price >= 0)
);

CREATE TABLE Inventory
(
    InventoryID INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    WarehouseID INT,
    ProductID   INT,
    Quantity    INT                            NOT NULL,
    FOREIGN KEY (WarehouseID) REFERENCES Warehouse (WarehouseID),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID),
    CONSTRAINT CHK_Quantity_Inventory CHECK (Quantity >= 0)
);

CREATE TABLE ProductSpecification
(
    SpecificationID    INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    ProductID          INT,
    SpecificationName  VARCHAR(100)                   NOT NULL,
    SpecificationValue VARCHAR(200)                   NOT NULL,
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID)
);

CREATE TABLE Supplier
(
    SupplierID INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    Name       VARCHAR(100)                   NOT NULL,
    Phone      VARCHAR(15),
    Address    VARCHAR(200),
    ContactName NVARCHAR(200) NULL,
    Email NVARCHAR(200) NULL,
    Note NVARCHAR(255) NULL,
    CreatedDate DATETIME2 DEFAULT GETDATE() NOT NULL,
    IsActive BIT DEFAULT 1 NOT NULL
);

CREATE TABLE Invoice
(
    InvoiceID   INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    CustomerID  INT,
    UserID      INT,
    Date        DATETIME                       NOT NULL,
    TotalAmount DECIMAL                        NOT NULL,
    Discount    DECIMAL     DEFAULT 0,
    Status      VARCHAR(20) DEFAULT 'Pending',
    CreatedAt   DATETIME    DEFAULT GETDATE(),
    UpdatedAt   DATETIME,
    UpdatedBy   INT,
    FOREIGN KEY (CustomerID) REFERENCES Customer (CustomerID),
    FOREIGN KEY (UserID) REFERENCES [User] (UserID),
    FOREIGN KEY (UpdatedBy) REFERENCES [User] (UserID)
);

CREATE TABLE InvoiceDetail
(
    InvoiceDetailID INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    InvoiceID       INT,
    ProductID       INT,
    Quantity        INT                            NOT NULL,
    UnitPrice       DECIMAL                        NOT NULL,
    Discount        DECIMAL DEFAULT 0,
    FOREIGN KEY (InvoiceID) REFERENCES Invoice (InvoiceID),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID),
    CONSTRAINT CHK_Quantity_InvoiceDetail CHECK (Quantity > 0),
    CONSTRAINT CHK_UnitPrice CHECK (UnitPrice >= 0)
);

CREATE TABLE StockEntry
(
    EntryID    INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    SupplierID INT,
    UserID     INT,
    Date       DATETIME                       NOT NULL,
    CreatedAt  DATETIME DEFAULT GETDATE(),
    UpdatedAt  DATETIME,
    UpdatedBy  INT,
    FOREIGN KEY (SupplierID) REFERENCES Supplier (SupplierID),
    FOREIGN KEY (UserID) REFERENCES [User] (UserID),
    FOREIGN KEY (UpdatedBy) REFERENCES [User] (UserID)
);

CREATE TABLE StockEntryDetail
(
    EntryDetailID INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    EntryID       INT,
    ProductID     INT,
    Quantity      INT                            NOT NULL,
    UnitCost      DECIMAL                        NOT NULL,
    FOREIGN KEY (EntryID) REFERENCES StockEntry (EntryID),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID),
    CONSTRAINT CHK_Quantity_StockEntryDetail CHECK (Quantity > 0),
    CONSTRAINT CHK_UnitCost CHECK (UnitCost >= 0)
);

CREATE TABLE Promotion
(
    PromotionID         INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    PromotionName       VARCHAR(100)                   NOT NULL,
    Description         VARCHAR(200),
    DiscountPercentage  DECIMAL,
    StartDate           DATETIME                       NOT NULL,
    EndDate             DATETIME                       NOT NULL,
    ApplicableProductID INT,
    FOREIGN KEY (ApplicableProductID) REFERENCES Product (ProductID)
);

CREATE TABLE Payment
(
    PaymentID     INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    InvoiceID     INT,
    PaymentMethod VARCHAR(50)                    NOT NULL,
    Amount        DECIMAL                        NOT NULL,
    PaymentDate   DATETIME                       NOT NULL,
    Status        VARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (InvoiceID) REFERENCES Invoice (InvoiceID)
);

CREATE TABLE PriceHistory
(
    PriceHistoryID INT PRIMARY KEY IDENTITY (1,1) NOT NULL,
    ProductID      INT,
    Price          DECIMAL                        NOT NULL,
    EffectiveDate  DATETIME                       NOT NULL,
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID)
);
CREATE TABLE Employee (
EmployeeID   INT PRIMARY KEY IDENTITY(1,1),
FullName     NVARCHAR(100) NOT NULL,
DateOfBirth  DATE,
IDCard       VARCHAR(20),           -- CCCD
Hometown     NVARCHAR(100),
Phone        VARCHAR(20),
Email        VARCHAR(100),
Status       VARCHAR(20) DEFAULT 'Active'
);
ALTER TABLE [User]
ADD EmployeeID INT;

ALTER TABLE [User]
ADD CONSTRAINT FK_User_Employee
FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID);

INSERT INTO [User] (Username, Password, Role) VALUES ('admin', '123456', 'Admin')
insert into [User] (Username, Password, Role) values ('staff', '123456', 'Staff')
insert into [User] (Username, Password, Role) values ('cashier', '123456', 'Cashier')
insert into [User] (Username, Password, Role) values ('warehouse', '123456', 'Warehouse')
