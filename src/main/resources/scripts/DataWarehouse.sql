create database DataWarehouse;
use DataWarehouse;

CREATE TABLE sales_fact (
                            sales_id INT AUTO_INCREMENT PRIMARY KEY,
                            date_id INT NOT NULL,
                            product_id INT NOT NULL,
                            store_id INT NOT NULL,
                            quantity_sold INT NOT NULL,
                            revenue DECIMAL(10, 2) NOT NULL
);

INSERT INTO sales_fact (date_id, product_id, store_id, quantity_sold, revenue)
VALUES
    (1, 1, 1, 10, 100.50), (2, 2, 2, 5, 50.75), (3, 3, 3, 15, 150.30),
    (4, 4, 1, 7, 70.00), (5, 5, 2, 20, 200.20), (6, 6, 3, 8, 80.80),
    (7, 7, 1, 12, 120.40), (8, 8, 2, 9, 90.60), (9, 9, 3, 14, 140.25),
    (10, 10, 1, 6, 60.10), (11, 11, 2, 18, 180.85), (12, 12, 3, 13, 130.95),
    (13, 13, 1, 10, 100.35), (14, 14, 2, 11, 110.55), (15, 15, 3, 19, 190.15),
    (16, 16, 1, 21, 210.70), (17, 17, 2, 23, 230.25), (18, 18, 3, 22, 220.95),
    (19, 19, 1, 25, 250.45), (20, 20, 2, 26, 260.65);

CREATE TABLE product_dim (
                             product_id INT AUTO_INCREMENT PRIMARY KEY,
                             product_name VARCHAR(50) NOT NULL,
                             category VARCHAR(50) NOT NULL,
                             brand VARCHAR(50) NOT NULL
);

INSERT INTO product_dim (product_name, category, brand)
VALUES
    ('Laptop', 'Electronics', 'Dell'), ('Smartphone', 'Electronics', 'Samsung'),
    ('Tablet', 'Electronics', 'Apple'), ('TV', 'Electronics', 'Sony'),
    ('Headphones', 'Accessories', 'Bose'), ('Speaker', 'Accessories', 'JBL'),
    ('Keyboard', 'Accessories', 'Logitech'), ('Mouse', 'Accessories', 'Razer'),
    ('Smartwatch', 'Wearables', 'Fitbit'), ('Fitness Tracker', 'Wearables', 'Garmin'),
    ('Camera', 'Electronics', 'Canon'), ('Printer', 'Electronics', 'HP'),
    ('Monitor', 'Electronics', 'Acer'), ('Router', 'Electronics', 'TP-Link'),
    ('Charger', 'Accessories', 'Anker'), ('Power Bank', 'Accessories', 'Xiaomi'),
    ('USB Drive', 'Accessories', 'SanDisk'), ('External HDD', 'Accessories', 'Seagate'),
    ('Projector', 'Electronics', 'Epson'), ('Drone', 'Electronics', 'DJI');

CREATE TABLE date_dim (
                          date_id INT AUTO_INCREMENT PRIMARY KEY,
                          date DATE NOT NULL,
                          month VARCHAR(20) NOT NULL,
                          quarter INT NOT NULL,
                          year INT NOT NULL
);

INSERT INTO date_dim (date, month, quarter, year)
VALUES
    ('2024-01-01', 'January', 1, 2024), ('2024-02-01', 'February', 1, 2024),
    ('2024-03-01', 'March', 1, 2024), ('2024-04-01', 'April', 2, 2024),
    ('2024-05-01', 'May', 2, 2024), ('2024-06-01', 'June', 2, 2024),
    ('2024-07-01', 'July', 3, 2024), ('2024-08-01', 'August', 3, 2024),
    ('2024-09-01', 'September', 3, 2024), ('2024-10-01', 'October', 4, 2024),
    ('2024-11-01', 'November', 4, 2024), ('2024-12-01', 'December', 4, 2024),
    ('2023-01-01', 'January', 1, 2023), ('2023-02-01', 'February', 1, 2023),
    ('2023-03-01', 'March', 1, 2023), ('2023-04-01', 'April', 2, 2023),
    ('2023-05-01', 'May', 2, 2023), ('2023-06-01', 'June', 2, 2023),
    ('2023-07-01', 'July', 3, 2023), ('2023-08-01', 'August', 3, 2023);

CREATE TABLE store_dim (
                           store_id INT AUTO_INCREMENT PRIMARY KEY,
                           store_name VARCHAR(50) NOT NULL,
                           region VARCHAR(50) NOT NULL
);

INSERT INTO store_dim (store_name, region)
VALUES
    ('Store A', 'North'), ('Store B', 'South'), ('Store C', 'East'),
    ('Store D', 'West'), ('Store E', 'North'), ('Store F', 'South'),
    ('Store G', 'East'), ('Store H', 'West'), ('Store I', 'North'),
    ('Store J', 'South'), ('Store K', 'East'), ('Store L', 'West'),
    ('Store M', 'North'), ('Store N', 'South'), ('Store O', 'East'),
    ('Store P', 'West'), ('Store Q', 'North'), ('Store R', 'South'),
    ('Store S', 'East'), ('Store T', 'West');

SELECT
    p.category,
    SUM(s.revenue) AS total_revenue
FROM
    sales_fact s
        JOIN
    product_dim p ON s.product_id = p.product_id
GROUP BY
    p.category
ORDER BY
    total_revenue DESC;

SELECT
    d.month,
    SUM(s.revenue) AS monthly_revenue
FROM
    sales_fact s
        JOIN
    date_dim d ON s.date_id = d.date_id
WHERE
    d.year = 2024
GROUP BY
    d.month
ORDER BY
    d.month;

SELECT
    p.product_name,
    SUM(s.revenue) AS total_revenue
FROM
    sales_fact s
        JOIN
    product_dim p ON s.product_id = p.product_id
GROUP BY
    p.product_name
ORDER BY
    total_revenue DESC
    LIMIT 5;

SELECT
    st.region,
    SUM(s.quantity_sold) AS total_quantity
FROM
    sales_fact s
        JOIN
    store_dim st ON s.store_id = st.store_id
GROUP BY
    st.region
ORDER BY
    total_quantity DESC;

SELECT
    st.store_name,
    SUM(s.revenue) AS store_revenue,
    ROUND(SUM(s.revenue) * 100.0 / SUM(SUM(s.revenue)) OVER (), 2) AS percentage_contribution
FROM
    sales_fact s
        JOIN
    store_dim st ON s.store_id = st.store_id
GROUP BY
    st.store_name
ORDER BY
    store_revenue DESC;

SELECT
    d.year,
    AVG(s.revenue) AS average_revenue_per_sale
FROM
    sales_fact s
        JOIN
    date_dim d ON s.date_id = d.date_id
GROUP BY
    d.year
ORDER BY
    d.year;

SELECT
    d.year,
    d.quarter,
    SUM(s.revenue) AS total_revenue
FROM
    sales_fact s
        JOIN
    date_dim d ON s.date_id = d.date_id
GROUP BY
    d.year, d.quarter
ORDER BY
    d.year, d.quarter;

SELECT
    p.category,
    p.product_name,
    SUM(s.quantity_sold) AS total_quantity,
    SUM(s.revenue) AS total_revenue
FROM
    sales_fact s
        JOIN
    product_dim p ON s.product_id = p.product_id
WHERE
    p.category = 'Electronics'
GROUP BY
    p.category, p.product_name
ORDER BY
    total_revenue DESC;

SELECT
    d1.year AS current_year,
    d2.year AS previous_year,
    SUM(s1.revenue) AS current_year_revenue,
    SUM(s2.revenue) AS previous_year_revenue,
    ROUND((SUM(s1.revenue) - SUM(s2.revenue)) * 100.0 / SUM(s2.revenue), 2) AS yoy_growth_percentage
FROM
    sales_fact s1
        JOIN
    date_dim d1 ON s1.date_id = d1.date_id
        LEFT JOIN
    sales_fact s2 ON s1.product_id = s2.product_id
        LEFT JOIN
    date_dim d2 ON s2.date_id = d2.date_id AND d2.year = d1.year - 1
WHERE
    d1.year IS NOT NULL
GROUP BY
    d1.year, d2.year
ORDER BY
    d1.year;

SELECT
    d.quarter,
    st.region,
    SUM(s.revenue) AS total_revenue
FROM
    sales_fact s
        JOIN
    date_dim d ON s.date_id = d.date_id
        JOIN
    store_dim st ON s.store_id = st.store_id
GROUP BY
    d.quarter, st.region
ORDER BY
    d.quarter, total_revenue DESC;

/*
### Overview of the Data Warehouse Schema

In a data warehouse, data is organized using **fact tables** and **dimension tables**. The schema here follows a star schema design, which consists of:

1. **Fact Table:** Contains numerical data and measures for analysis (e.g., `sales_fact` table).
2. **Dimension Tables:** Describe the context of the facts and are used for filtering and aggregation (e.g., `product_dim`, `date_dim`, `store_dim` tables).

---

### **Tables and Their Roles**

#### 1. `sales_fact` (Fact Table)
- **Purpose:** Stores the core transactional data.
- **Columns:**
  - `sales_id`: Unique identifier for each sale.
  - `date_id`: Links to `date_dim` for time-based analysis.
  - `product_id`: Links to `product_dim` for product-specific details.
  - `store_id`: Links to `store_dim` for store-specific details.
  - `quantity_sold`: Number of units sold.
  - `revenue`: Total revenue generated by the sale.

#### 2. `product_dim` (Dimension Table)
- **Purpose:** Provides detailed information about products.
- **Columns:**
  - `product_id`: Unique identifier for each product.
  - `product_name`: Name of the product (e.g., "Laptop").
  - `category`: Product category (e.g., "Electronics").
  - `brand`: Brand of the product (e.g., "Dell").

#### 3. `date_dim` (Dimension Table)
- **Purpose:** Provides a time-based context for analysis.
- **Columns:**
  - `date_id`: Unique identifier for each date.
  - `date`: The specific date (e.g., "2024-01-01").
  - `month`: Month name (e.g., "January").
  - `quarter`: Quarter of the year (1, 2, 3, or 4).
  - `year`: Year value (e.g., "2024").

#### 4. `store_dim` (Dimension Table)
- **Purpose:** Provides information about stores where sales occur.
- **Columns:**
  - `store_id`: Unique identifier for each store.
  - `store_name`: Name of the store (e.g., "Store A").
  - `region`: Region where the store is located (e.g., "North").

---

### **Queries and Their Functions**

#### **1. Total Revenue Across All Stores**
```sql
SELECT SUM(revenue) AS total_revenue
FROM sales_fact;
```
- **What It Does:** Aggregates the total revenue generated across all sales.

---

#### **2. Revenue Breakdown by Product**
```sql
SELECT p.product_name, SUM(s.revenue) AS total_revenue
FROM sales_fact s
JOIN product_dim p ON s.product_id = p.product_id
GROUP BY p.product_name
ORDER BY total_revenue DESC;
```
- **What It Does:** Shows the revenue generated by each product, sorted from highest to lowest.

---

#### **3. Monthly Revenue**
```sql
SELECT d.month, d.year, SUM(s.revenue) AS total_revenue
FROM sales_fact s
JOIN date_dim d ON s.date_id = d.date_id
GROUP BY d.year, d.month
ORDER BY d.year, d.month;
```
- **What It Does:** Aggregates revenue for each month in the dataset.

---

#### **4. Revenue by Store Region**
```sql
SELECT st.region, SUM(s.revenue) AS total_revenue
FROM sales_fact s
JOIN store_dim st ON s.store_id = st.store_id
GROUP BY st.region
ORDER BY total_revenue DESC;
```
- **What It Does:** Provides a breakdown of revenue by store region, highlighting which regions perform best.

---

#### **5. Top-Selling Products by Quantity**
```sql
SELECT p.product_name, SUM(s.quantity_sold) AS total_quantity
FROM sales_fact s
JOIN product_dim p ON s.product_id = p.product_id
GROUP BY p.product_name
ORDER BY total_quantity DESC
LIMIT 5;
```
- **What It Does:** Identifies the top 5 products with the highest sales quantities.

---

#### **6. Quarterly Revenue**
```sql
SELECT d.year, d.quarter, SUM(s.revenue) AS total_revenue
FROM sales_fact s
JOIN date_dim d ON s.date_id = d.date_id
GROUP BY d.year, d.quarter
ORDER BY d.year, d.quarter;
```
- **What It Does:** Aggregates revenue for each quarter, providing insights into seasonal trends.

---

#### **7. Revenue for Specific Product Categories**
```sql
SELECT p.category, SUM(s.revenue) AS total_revenue
FROM sales_fact s
JOIN product_dim p ON s.product_id = p.product_id
GROUP BY p.category
ORDER BY total_revenue DESC;
```
- **What It Does:** Shows revenue generated by each product category, helping identify the most profitable categoryIds.

---

#### **8. Store Performance by Revenue**
```sql
SELECT st.store_name, SUM(s.revenue) AS total_revenue
FROM sales_fact s
JOIN store_dim st ON s.store_id = st.store_id
GROUP BY st.store_name
ORDER BY total_revenue DESC;
```
- **What It Does:** Displays the revenue generated by each store, ranked by performance.

---

### **How the Schema and Queries Work Together**

1. **Fact Table (`sales_fact`):** Stores numeric data (e.g., revenue, quantity sold) that will be aggregated.
2. **Dimension Tables (`product_dim`, `date_dim`, `store_dim`):** Provide context for analysis by linking to the fact table.
3. **Joins in Queries:** Combine fact and dimension tables to enrich the data and perform multidimensional analysis.
4. **Group By and Aggregations:** Used to summarize data (e.g., total revenue, quantity sold) for specific dimensions.

This schema and its queries are designed to answer typical analytical questions about sales, products, stores, and time-based performance in a business. Let me know if you need more examples or deeper analysis!
*/