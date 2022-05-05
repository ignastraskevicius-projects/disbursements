DELIMITER //
CREATE PROCEDURE calculate_disbursements_over_week_period_ending_on(
    IN last_day DATE
) BEGIN
    INSERT INTO disbursement_over_week_period (
        disbursement_amount,
        last_day_of_week_period,
        merchant_id
    ) SELECT
            amount_per_merchant.amounts_summed,
            last_day,
            merchant.external_merchant_id
        FROM (
            SELECT
                    merchant_id,
                    SUM(amount) * 0.9900 AS amounts_summed
                FROM completed_order
                WHERE completion_date BETWEEN DATE_SUB(last_day, INTERVAL 6 DAY) AND last_day
                    AND amount < 50
                GROUP BY merchant_id
            UNION
            SELECT
                    merchant_id,
                    SUM(amount) * 0.9905 AS amounts_summed
                FROM completed_order
                WHERE completion_date BETWEEN DATE_SUB(last_day, INTERVAL 6 DAY) AND last_day
                    AND amount >= 50 AND amount < 300
                GROUP BY merchant_id
            UNION
            SELECT
                    merchant_id,
                    SUM(amount) * 0.9915 AS amounts_summed
                FROM completed_order
                WHERE completion_date BETWEEN DATE_SUB(last_day, INTERVAL 6 DAY) AND last_day
                    AND amount >= 300
                GROUP BY merchant_id
            ) AS amount_per_merchant
            INNER JOIN
            merchant
            ON merchant.id = amount_per_merchant.merchant_id;
END //
DELIMITER ;
