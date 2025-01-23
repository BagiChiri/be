create database StarSchema;
use StarSchema;

-- Dimension Table: Product
CREATE TABLE product_dim (
                             product_id INT PRIMARY KEY,
                             product_name NVARCHAR(100) NOT NULL,
                             category NVARCHAR(50),
                             brand NVARCHAR(50),
                             price DECIMAL(10, 2)
);

-- Dimension Table: Customer
CREATE TABLE customer_dim (
                              customer_id INT PRIMARY KEY,
                              first_name NVARCHAR(50),
                              last_name NVARCHAR(50),
                              email NVARCHAR(100),
                              phone NVARCHAR(15),
                              region NVARCHAR(50),
                              gender NVARCHAR(10)
);

-- Dimension Table: Store
CREATE TABLE store_dim (
                           store_id INT PRIMARY KEY,
                           store_name NVARCHAR(100),
                           location NVARCHAR(100),
                           region NVARCHAR(50),
                           manager NVARCHAR(50)
);

-- Dimension Table: Date
CREATE TABLE date_dim (
                          date_id INT PRIMARY KEY,
                          full_date DATE NOT NULL,
                          day_of_week NVARCHAR(15),
                          month NVARCHAR(20),
                          quarter INT,
                          year INT
);

-- Fact Table: Sales
CREATE TABLE sales_fact (
                            sales_id INT PRIMARY KEY,
                            product_id INT NOT NULL,
                            customer_id INT NOT NULL,
                            store_id INT NOT NULL,
                            date_id INT NOT NULL,
                            quantity_sold INT NOT NULL,
                            revenue DECIMAL(15, 2) NOT NULL,
                            FOREIGN KEY (product_id) REFERENCES product_dim(product_id),
                            FOREIGN KEY (customer_id) REFERENCES customer_dim(customer_id),
                            FOREIGN KEY (store_id) REFERENCES store_dim(store_id),
                            FOREIGN KEY (date_id) REFERENCES date_dim(date_id)
);

-- Product Dimension
INSERT INTO product_dim VALUES
                            (1, 'Laptop', 'Electronics', 'Dell', 800.00),
                            (2, 'Smartphone', 'Electronics', 'Samsung', 600.00),
                            (3, 'Washing Machine', 'Appliances', 'LG', 500.00);

-- Customer Dimension
INSERT INTO customer_dim VALUES
                             (1, 'John', 'Doe', 'john.doe@example.com', '1234567890', 'North', 'Male'),
                             (2, 'Jane', 'Smith', 'jane.smith@example.com', '0987654321', 'South', 'Female');

-- Store Dimension
INSERT INTO store_dim VALUES
                          (1, 'Store A', 'New York', 'North', 'Alice'),
                          (2, 'Store B', 'Los Angeles', 'West', 'Bob');

-- Date Dimension
INSERT INTO date_dim VALUES
                         (1, '2024-01-01', 'Monday', 'January', 1, 2024),
                         (2, '2024-01-02', 'Tuesday', 'January', 1, 2024);

-- Fact Table: Sales
INSERT INTO sales_fact VALUES
                           (1, 1, 1, 1, 1, 2, 1600.00), -- John bought 2 Dell laptops from Store A on 2024-01-01
                           (2, 2, 2, 2, 2, 1, 600.00);  -- Jane bought 1 Samsung smartphone from Store B on 2024-01-02

SELECT p.product_name, SUM(s.revenue) AS total_revenue
FROM sales_fact s
         JOIN product_dim p ON s.product_id = p.product_id
GROUP BY p.product_name
ORDER BY total_revenue DESC;

SELECT st.region, SUM(s.revenue) AS total_revenue
FROM sales_fact s
         JOIN store_dim st ON s.store_id = st.store_id
GROUP BY st.region
ORDER BY total_revenue DESC;

SELECT d.month, d.year, SUM(s.revenue) AS total_revenue
FROM sales_fact s
         JOIN date_dim d ON s.date_id = d.date_id
GROUP BY d.month, d.year
ORDER BY d.year, d.month;

SELECT c.first_name + ' ' + c.last_name AS customer_name, SUM(s.revenue) AS total_revenue
FROM sales_fact s
         JOIN customer_dim c ON s.customer_id = c.customer_id
GROUP BY c.first_name, c.last_name
ORDER BY total_revenue DESC;

