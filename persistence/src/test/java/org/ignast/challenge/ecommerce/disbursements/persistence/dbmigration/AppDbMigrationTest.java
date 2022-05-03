package org.ignast.challenge.ecommerce.disbursements.persistence.dbmigration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.challenge.ecommerce.disbursements.persistence.dbmigration.AppDbContainer.getDataSourceTo;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import lombok.val;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class AppDbMigrationTest {

    @Container
    private static final MySQLContainer APP_DB = AppDbContainer.singleton();

    private static JdbcTemplate db;

    @BeforeAll
    public static void setup() {
        final val dataSource = getDataSourceTo(APP_DB);
        db = new JdbcTemplate(dataSource);
        Flyway.configure().dataSource(getDataSourceTo(APP_DB)).load().migrate();
    }

    @AfterEach
    public void emptyDisbursements() {
        db.execute("DELETE FROM disbursements_over_week");
    }

    @Test
    public void shouldAcceptDisbursement() {
        val sql =
            """
                    INSERT INTO disbursements_over_week (merchant_id, last_day_of_week, disbursement_amount) 
                    VALUES ('amazon@amazon.com', '2022-01-01', 58.1234567)""";
        db.execute(sql);
    }

    @Test
    public void disbursementShouldAccommodateLongEnoughMerchantId() {
        val merchantId = "a".repeat(256);
        val sql = String.format(
            """
                        INSERT INTO disbursements_over_week (merchant_id, last_day_of_week, disbursement_amount) 
                        VALUES ('%s', '2022-01-01', 58.1234567)""",
            merchantId
        );
        db.execute(sql);
    }

    @Test
    public void disbursementShouldPreserveOriginal6FractionalDigitsPotentiallyRoundingThe7th() {
        val amount = "8.12345678";
        val sql = String.format(
            """
                        INSERT INTO disbursements_over_week (merchant_id, last_day_of_week, disbursement_amount) 
                        VALUES ('amazon@amazon.com', '2022-01-01', %s)""",
            amount
        );
        db.execute(sql);
        BigDecimal retrievedAmount = db.queryForObject(
            "SELECT disbursement_amount FROM disbursements_over_week",
            BigDecimal.class
        );
        assertThat(retrievedAmount.toPlainString()).isEqualTo("8.1234568");
    }

    @Test
    public void disbursementShouldContainSingleAmountPerMerchantPerDate() {
        String sql =
            """
                INSERT INTO disbursements_over_week (merchant_id, last_day_of_week, disbursement_amount) 
                VALUES ('amazon@amazon.com', '2022-01-01', 100)""";
        db.execute(sql);
        assertThatExceptionOfType(DuplicateKeyException.class)
            .isThrownBy(() -> db.execute(sql))
            .withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
            .havingRootCause()
            .withMessageContaining(
                "Duplicate entry 'amazon@amazon.com-2022-01-01' for key 'disbursements_over_week.unique_date_merchant'"
            );
    }
}
