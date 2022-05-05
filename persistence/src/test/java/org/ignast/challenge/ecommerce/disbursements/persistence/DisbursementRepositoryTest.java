package org.ignast.challenge.ecommerce.disbursements.persistence;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DisbursementRepositoryTest {

    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer(
        DockerImageName.parse("mysql:8.0.28-debian")
    )
        .withDatabaseName("disbursements")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private JpaDisbursementsRepository repository;

    private final JdbcTemplate jdbcTemplate;

    public DisbursementRepositoryTest() throws SQLException {
        final val testDataSource = new SingleConnectionDataSource();
        testDataSource.setUrl(MYSQL.getJdbcUrl());
        testDataSource.setUsername(MYSQL.getUsername());
        testDataSource.setPassword(MYSQL.getPassword());
        jdbcTemplate = new JdbcTemplate(testDataSource);
    }

    @DynamicPropertySource
    private static void registedDatasource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl());
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "test");
    }

    @AfterEach
    public void emptyTables() {
        jdbcTemplate.execute("DELETE FROM completed_order");
        jdbcTemplate.execute("DELETE FROM merchant");
        jdbcTemplate.execute("DELETE FROM disbursement_over_week_period");
    }

    @Test
    public void shouldCalculateDisbursements() {
        jdbcTemplate.execute(
            """
                    INSERT INTO merchant (id, external_merchant_id) 
                    VALUES (1, 'amazon@amazon.com')"""
        );
        jdbcTemplate.execute(
            """
                            INSERT INTO completed_order (id, merchant_id, amount, completion_date) 
                            VALUES (1, 1, 100.01, '2022-01-01')"""
        );

        repository.calculateDisbursementsForWeekEndingWith(LocalDate.of(2022, 1, 1));

        final val disbursements = repository.findByLastDayOfWeekPeriod(LocalDate.of(2022, 1, 1));
        assertThat(disbursements).hasSize(1);
        disbursements
            .stream()
            .forEach(d -> {
                assertThat(d.getAmount().getNumberStripped()).isEqualTo(new BigDecimal("99.059905"));
                assertThat(d.getAmount().getCurrency().getCurrencyCode()).isEqualTo("EUR");
                assertThat(d.getExternalMerchantId()).isEqualTo("amazon@amazon.com");
            });
    }
}
