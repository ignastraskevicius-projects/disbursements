CREATE TABLE disbursements_over_week (
    id integer AUTO_INCREMENT PRIMARY KEY,
    merchant_id VARCHAR(256),
    last_day_of_week DATE,
    disbursement_amount DECIMAL(20,7),
    CONSTRAINT unique_date_merchant UNIQUE(merchant_id, last_day_of_week),
    INDEX (last_day_of_week)
);