package org.ignast.challenge.ecommerce.disbursements.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
class DisbursementsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test() throws Exception {
        mockMvc
            .perform(get("/disbursements").accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
            .andExpect(content().json(amazon()));
    }

    private final String amazon() {
        return """
                {
                    "timeFrame":{
                        "start":"2012-03-22T00:00:00-00:00",
                        "end":"2012-03-29T00:00:00-00:00"
                    },
                    "merchants":[{
                        "id":"amazon@amazon.com",
                        "monetaryValue":{
                            "amount":"5.27854324",
                            "currency":"EUR"
                        }
                    }]
                    
                }""";
    }
}
