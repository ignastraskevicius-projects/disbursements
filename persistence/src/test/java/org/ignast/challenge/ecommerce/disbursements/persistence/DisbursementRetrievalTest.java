package org.ignast.challenge.ecommerce.disbursements.persistence;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.sql.DataSource;
import lombok.val;
import org.h2.tools.Server;
import org.ignast.challenge.ecommerce.disbursements.persistence.dbmigration.AppDbContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
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
public class DisbursementRetrievalTest {

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

    public DisbursementRetrievalTest() throws SQLException {
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
