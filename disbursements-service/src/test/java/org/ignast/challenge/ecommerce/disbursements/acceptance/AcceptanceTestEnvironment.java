package org.ignast.challenge.ecommerce.disbursements.acceptance;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class AcceptanceTestEnvironment {

    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer(
        DockerImageName.parse(System.getProperty("mysqldev.image")).asCompatibleSubstituteFor("mysql")
    )
        .withPassword("test");

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    private static void registedDatasource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl().replace("/test", "/disbursements"));
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "test");
    }
}
