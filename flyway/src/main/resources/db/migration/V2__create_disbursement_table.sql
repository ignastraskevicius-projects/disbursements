CREATE TABLE disbursement_over_week_period (
    id integer AUTO_INCREMENT PRIMARY KEY,
    external_merchant_id VARCHAR(256),
    last_day_of_week_period DATE,
    disbursable_amount DECIMAL(20,7),
    CONSTRAINT unique_date_merchant UNIQUE(external_merchant_id, last_day_of_week_period)
);
CREATE INDEX disbursement_days ON disbursement_over_week_period(last_day_of_week_period);
