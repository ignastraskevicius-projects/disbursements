package org.ignast.challenge.ecommerce.disbursements.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.val;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DisbursementsResourceTest extends AcceptanceTestEnvironment {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        restTemplate = restTemplateBuilder.build();
    }

    @AfterEach
    public void cleanupDatabase() {
        jdbcTemplate.execute("DELETE FROM disbursement_over_week_period;");
    }

    @Test
    public void shouldQueryResource() throws JSONException {
        jdbcTemplate.execute(
            """
                    INSERT INTO disbursement_over_week_period (merchant_id, last_day_of_week_period, disbursement_amount) 
                    VALUES ('amazon@amazon.com', '2022-01-07', 5.2785432)"""
        );

        val result = restTemplate.exchange(query("2022-01-08"), HttpMethod.GET, asJson(), String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
        assertEquals(amazon(), result.getBody(), NON_EXTENSIBLE);
    }

    private final String amazon() {
        return """
                {
                    "timeFrame":{
                        "start":"2022-01-01T00:00:00+01:00",
                        "end":"2022-01-08T00:00:00+01:00"
                    },
                    "merchants":[{
                        "merchantId":"amazon@amazon.com",
                        "monetaryValue":{
                            "amount":5.2785432,
                            "currency":"EUR"
                        }
                    }]
                    
                }""";
    }

    private final HttpEntity<String> asJson() {
        final val headers = new HttpHeaders();
        headers.add("Accept", APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }

    private final String query(final String dayAfterPeriod) {
        return String.format(
            "http://localhost:%d/disbursements?timeFrameEndingBefore=%s&timeFrame=1week",
            port,
            dayAfterPeriod
        );
    }
}
