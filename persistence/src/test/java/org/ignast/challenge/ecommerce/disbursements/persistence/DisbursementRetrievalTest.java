package org.ignast.challenge.ecommerce.disbursements.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.sql.DataSource;
import lombok.val;
import org.h2.tools.Server;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DisbursementRetrievalTest {

    @Autowired
    private JpaDisbursementsRepository repository;

    private final JdbcTemplate jdbcTemplate;

    public DisbursementRetrievalTest(@Autowired final DataSource dataSource) throws SQLException {
        final val testDataSource = new SingleConnectionDataSource();
        testDataSource.setUrl(dataSource.getConnection().getMetaData().getURL());
        testDataSource.setUsername(dataSource.getConnection().getMetaData().getUserName());
        jdbcTemplate = new JdbcTemplate(testDataSource);
    }

    @Test
    public void shouldFindDisbursementsByDate() {
        jdbcTemplate.execute(
            """
                    INSERT INTO disbursement_over_week_period (merchant_id, last_day_of_week_period, disbursement_amount)
                    VALUES ('amazon@amazon.com', '2022-01-01', 58.1234561)"""
        );

        final val disbursements = repository.findByLastDayOfWeekPeriod(LocalDate.of(2022, 1, 1));
        assertThat(disbursements).hasSize(1);
        disbursements
            .stream()
            .forEach(d -> {
                assertThat(d.getAmount().getNumberStripped()).isEqualTo(new BigDecimal("58.1234561"));
                assertThat(d.getAmount().getCurrency().getCurrencyCode()).isEqualTo("EUR");
                assertThat(d.getMerchantId()).isEqualTo("amazon@amazon.com");
            });
    }
}
