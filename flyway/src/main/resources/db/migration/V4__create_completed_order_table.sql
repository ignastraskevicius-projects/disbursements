CREATE TABLE completed_order (
    id INTEGER PRIMARY KEY,
    merchant_id INTEGER,
    amount DECIMAL(15,2),
    completion_date DATE,
    FOREIGN KEY (merchant_id) REFERENCES merchant(id),
    INDEX date_amount (completion_date, amount)
)