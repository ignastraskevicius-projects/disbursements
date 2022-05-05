package org.ignast.challenge.ecommerce.disbursements.dbmigration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.challenge.ecommerce.disbursements.dbmigration.AppDbContainer.getDataSourceTo;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
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

    @Nested
    final class DisbursementMigrationTest {

        @AfterEach
        public void emptyDisbursements() {
            db.execute("DELETE FROM disbursement_over_week_period");
        }

        @Test
        public void shouldAcceptDisbursement() {
            val sql =
                """
                            INSERT INTO disbursement_over_week_period (external_merchant_id, last_day_of_week_period, disbursable_amount) 
                            VALUES ('amazon@amazon.com', '2022-01-01', 58.1234567)""";
            db.execute(sql);
        }

        @Test
        public void disbursementShouldAccommodateLongEnoughMerchantId() {
            val externalMerchantId = "a".repeat(256);
            val sql = String.format(
                """
                                INSERT INTO disbursement_over_week_period (external_merchant_id, last_day_of_week_period, disbursable_amount) 
                                VALUES ('%s', '2022-01-01', 58.1234567)""",
                externalMerchantId
            );
            db.execute(sql);
        }

        @Test
        public void disbursementShouldPreserveOriginal6FractionalDigitsPotentiallyRoundingThe7th() {
            val amount = "8.12345678";
            val sql = String.format(
                """
                                INSERT INTO disbursement_over_week_period (external_merchant_id, last_day_of_week_period, disbursable_amount) 
                                VALUES ('amazon@amazon.com', '2022-01-01', %s)""",
                amount
            );
            db.execute(sql);
            BigDecimal retrievedAmount = db.queryForObject(
                "SELECT disbursable_amount FROM disbursement_over_week_period",
                BigDecimal.class
            );
            assertThat(retrievedAmount.toPlainString()).isEqualTo("8.1234568");
        }

        @Test
        public void disbursementShouldContainSingleAmountPerMerchantPerDate() {
            String sql =
                """
                        INSERT INTO disbursement_over_week_period (external_merchant_id, last_day_of_week_period, disbursable_amount) 
                        VALUES ('amazon@amazon.com', '2022-01-01', 100)""";
            db.execute(sql);
            assertThatExceptionOfType(DuplicateKeyException.class)
                .isThrownBy(() -> db.execute(sql))
                .withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                .havingRootCause()
                .withMessageContaining(
                    "Duplicate entry 'amazon@amazon.com-2022-01-01' for key 'disbursement_over_week_period.unique_date_merchant'"
                );
        }

        @Test
        public void shouldAutoincrementId() {
            final val insertDisbursementAmazon =
                """
                            INSERT INTO disbursement_over_week_period (external_merchant_id, last_day_of_week_period, disbursable_amount) 
                            VALUES ('amazon@amazon.com', '2022-01-01', 58.1234567)""";
            final val insertDisbursementMicrosoft =
                """
                            INSERT INTO disbursement_over_week_period (external_merchant_id, last_day_of_week_period, disbursable_amount) 
                            VALUES ('microsoft@microsoft.com', '2022-01-01', 58.1234567)""";
            db.execute(insertDisbursementAmazon);
            db.execute(insertDisbursementMicrosoft);

            final val amazonDisbursementId = db.queryForObject(
                "SELECT id FROM disbursement_over_week_period WHERE external_merchant_id = 'amazon@amazon.com'",
                Integer.class
            );
            final val microsoftDisbursementId = db.queryForObject(
                "SELECT id FROM disbursement_over_week_period WHERE external_merchant_id = 'microsoft@microsoft.com'",
                Integer.class
            );
            Assertions.assertThat(amazonDisbursementId).isGreaterThan(0);
            Assertions.assertThat(microsoftDisbursementId).isGreaterThan(0);
            assertThat(amazonDisbursementId + 1).isEqualTo(microsoftDisbursementId);
        }
    }

    @Nested
    final class MerchantMigrationTest {

        @AfterEach
        public void emptyTable() {
            db.execute("DELETE FROM merchant");
        }

        @Test
        public void shouldAcceptMerchant() {
            val sql =
                """
                            INSERT INTO merchant (id, external_merchant_id) 
                            VALUES (1, 'amazon@amazon.com')""";
            db.execute(sql);
        }

        @Test
        public void shouldAccommodateLongEnoughMerchantId() {
            val externalMerchantId = "a".repeat(256);
            val sql = String.format(
                """
                                INSERT INTO merchant (id, external_merchant_id) 
                                VALUES (1, '%s')""",
                externalMerchantId
            );
            db.execute(sql);
        }
    }

    @Nested
    final class OderMigrationTest {

        @AfterEach
        public void emptyTables() {
            db.execute("DELETE FROM completed_order");
            db.execute("DELETE FROM merchant");
        }

        @Test
        public void shouldAcceptOrder() {
            db.execute(
                """
                            INSERT INTO merchant (id, external_merchant_id) 
                            VALUES (1, 'amazon@amazon.com')"""
            );
            val sql =
                """
                            INSERT INTO completed_order (id, merchant_id, amount, completion_date) 
                            VALUES (1, 1, 58.12, '2022-01-01')""";
            db.execute(sql);
        }

        @Test
        public void shouldNotAcceptOrderForNonexistentMerchant() {
            val sql =
                """
                                INSERT INTO completed_order (id, merchant_id, amount, completion_date) 
                                VALUES (1, 1, 58.12, '2022-01-01')""";
            assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> db.execute(sql))
                .withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                .havingRootCause()
                .withMessageContaining("FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)");
        }
    }

    @Nested
    final class DisbursementCalculatingProcedure {

        private SimpleJdbcCall proc = new SimpleJdbcCall(db);

        private final MerchantAndOrdersSetup setup = new MerchantAndOrdersSetup();

        @AfterEach
        public void emptyTables() {
            db.execute("DELETE FROM completed_order");
            db.execute("DELETE FROM merchant");
            db.execute("DELETE FROM disbursement_over_week_period");
        }

        @ParameterizedTest
        @ValueSource(strings = { "0.01", "49.99", "50.00", "299.99", "300.00" })
        public void shouldNotTakeOrdersNewerThanDateProvidedForEveryTranches(final String amount) {
            setup.amazonOrder(amount, "2022-01-01");
            setup.microsoftOrder(amount, "2022-01-02");

            proc
                .withProcedureName("calculate_disbursements_over_week_period_ending_on")
                .execute(Map.of("last_day", LocalDate.of(2022, 1, 1)));

            assertThat(
                db.queryForObject(
                    "SELECT external_merchant_id FROM disbursement_over_week_period",
                    String.class
                )
            )
                .isEqualTo("amazon@amazon.com");
        }

        @ParameterizedTest
        @ValueSource(strings = { "0.01", "49.99", "50.00", "299.99", "300.00" })
        public void shouldNotTakeOrdersOlderThanWeekFromDateProvidedDateBeingALastDayOfThatWeekForAllTranches(
            final String amount
        ) {
            setup.amazonOrder(amount, "2022-01-01");
            setup.microsoftOrder(amount, "2022-01-02");

            proc
                .withProcedureName("calculate_disbursements_over_week_period_ending_on")
                .execute(Map.of("last_day", LocalDate.of(2022, 1, 8)));

            assertThat(
                db.queryForObject(
                    "SELECT external_merchant_id FROM disbursement_over_week_period",
                    String.class
                )
            )
                .isEqualTo("microsoft@microsoft.com");
        }

        @Test
        public void disbursableAmountForOrdersLessThan50ShouldBe99PercentFromTotal() {
            setup.amazonOrders("49.99", "2022-01-01", "0.02", "2022-01-01");

            proc
                .withProcedureName("calculate_disbursements_over_week_period_ending_on")
                .execute(Map.of("last_day", LocalDate.of(2022, 1, 1)));

            assertThat(
                new BigDecimal(
                    db.queryForObject(
                        "SELECT disbursable_amount FROM disbursement_over_week_period",
                        String.class
                    )
                )
                    .setScale(4)
            )
                .isEqualTo("49.5099");
        }

        @Test
        public void disbursableAmountForOrdersLessThan300ShouldBe99_05PercentFromTotal() {
            setup.amazonOrders("299.99", "2022-01-01", "50", "2022-01-01");

            proc
                .withProcedureName("calculate_disbursements_over_week_period_ending_on")
                .execute(Map.of("last_day", LocalDate.of(2022, 1, 1)));

            assertThat(
                new BigDecimal(
                    db.queryForObject(
                        "SELECT disbursable_amount FROM disbursement_over_week_period",
                        String.class
                    )
                )
                    .setScale(6)
            )
                .isEqualTo("346.665095");
        }

        @Test
        public void disbursableAmountForOrdersGte300ShouldBe99_15PercentFromTotal() {
            setup.amazonOrders("300.00", "2022-01-01", "300.01", "2022-01-01");

            proc
                .withProcedureName("calculate_disbursements_over_week_period_ending_on")
                .execute(Map.of("last_day", LocalDate.of(2022, 1, 1)));

            assertThat(
                new BigDecimal(
                    db.queryForObject(
                        "SELECT disbursable_amount FROM disbursement_over_week_period",
                        String.class
                    )
                )
                    .setScale(6)
            )
                .isEqualTo("594.909915");
        }

        private class MerchantAndOrdersSetup {

            private int id = 1;

            private void amazonOrder(final String amount, final String date) {
                order("amazon@amazon.com", amount, date);
            }

            private void amazonOrders(
                final String amount1,
                final String date1,
                final String amount2,
                final String date2
            ) {
                orders("amazon@amazon.com", amount1, date1, amount2, date2);
            }

            private void microsoftOrder(final String amount, final String date) {
                order("microsoft@microsoft.com", amount, date);
            }

            private void order(final String externalMerchantId, final String amount, final String date) {
                db.execute(
                    String.format(
                        """
                    INSERT INTO merchant (id, external_merchant_id) 
                    VALUES (%d, '%s')""",
                        id,
                        externalMerchantId
                    )
                );
                db.execute(
                    String.format(
                        """
                            INSERT INTO completed_order (id, merchant_id, amount, completion_date) 
                            VALUES (%d, %d, %s, '%s')""",
                        id + 1,
                        id,
                        amount,
                        date
                    )
                );
                id = id + 2;
            }

            private void orders(
                final String externalMerchantId,
                final String amount1,
                final String date1,
                final String amount2,
                final String date2
            ) {
                db.execute(
                    String.format(
                        """
                    INSERT INTO merchant (id, external_merchant_id) 
                    VALUES (%d, '%s')""",
                        id,
                        externalMerchantId
                    )
                );
                db.execute(
                    String.format(
                        """
                            INSERT INTO completed_order (id, merchant_id, amount, completion_date) 
                            VALUES (%d, %d, %s, '%s')""",
                        id + 1,
                        id,
                        amount1,
                        date1
                    )
                );
                db.execute(
                    String.format(
                        """
                            INSERT INTO completed_order (id, merchant_id, amount, completion_date) 
                            VALUES (%d, %d, %s, '%s')""",
                        id + 2,
                        id,
                        amount2,
                        date2
                    )
                );
                id = id + 3;
            }
        }
    }
}
