package org.ignast.challenge.ecommerce.disbursements.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursement;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursements;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class DisbursementsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Disbursements disbursements;

    @Test
    public void test() throws Exception {
        when(disbursements.retrieveDisbursementsOverWeekEndingAt(any()))
            .thenReturn(
                List.of(new Disbursement("amazon@amazon.com", Money.of(new BigDecimal("5.27854324"), "EUR")))
            );
        mockMvc
            .perform(
                get("/disbursements?endingAt=2012-03-29T00:00:00-00:00&timeFrame=1week")
                    .accept(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
            .andExpect(content().json(amazon()));
    }

    private final String amazon() {
        return """
                {
                    "timeFrame":{
                        "start":"2012-03-22T00:00:00+01:00",
                        "end":"2012-03-29T00:00:00+01:00"
                    },
                    "merchants":[{
                        "merchantId":"amazon@amazon.com",
                        "monetaryValue":{
                            "amount":5.27854324,
                            "currency":"EUR"
                        }
                    }]
                    
                }""";
    }
}
