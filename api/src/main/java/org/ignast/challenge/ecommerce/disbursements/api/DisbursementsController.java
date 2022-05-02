package org.ignast.challenge.ecommerce.disbursements.api;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/disbursements")
public class DisbursementsController {

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public String retrieveDisbursements() {
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
