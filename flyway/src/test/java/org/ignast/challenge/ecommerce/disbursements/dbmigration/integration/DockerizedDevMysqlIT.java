package org.ignast.challenge.ecommerce.disbursements.dbmigration.integration;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public final class DockerizedDevMysqlIT {

    @Container
    @SuppressWarnings("rawtypes")
    public static final MySQLContainer MYSQL = new MySQLContainer(
        DockerImageName.parse(System.getProperty("docker.image")).asCompatibleSubstituteFor("mysql")
    )
        .withPassword("test");

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(createDataSource());

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldCreateCompany() {
        jdbcTemplate.execute(
            """
                        INSERT INTO disbursement_over_week_period (id, merchant_id, last_day_of_week_period, disbursement_amount) 
                        VALUES (1, 'amazon@amazon.com', '2022-01-01', 58.1234567)"""
        );

        final val name = jdbcTemplate.queryForObject(
            "SELECT merchant_id FROM disbursement_over_week_period WHERE id = 1",
            String.class
        );

        Assertions.assertThat(name).isEqualTo("amazon@amazon.com");
    }

    private DataSource createDataSource() {
        final val dataSource = new SingleConnectionDataSource();
        dataSource.setUrl(MYSQL.getJdbcUrl().replaceFirst("/test", "/disbursements"));
        dataSource.setUsername("root");
        dataSource.setPassword("test");
        return dataSource;
    }
}
