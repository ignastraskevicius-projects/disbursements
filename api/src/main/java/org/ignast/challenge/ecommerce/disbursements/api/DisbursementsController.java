package org.ignast.challenge.ecommerce.disbursements.api;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursement;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/disbursements")
public class DisbursementsController {

    @Autowired
    private Disbursements disbursements;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public DisbursementsDto retrieveDisbursements() {
        List<Disbursement> disbursementsOverTheWeek = disbursements.retrieveDisbursementsOverWeekEndingAt(
            ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
        );
        return new DisbursementsDto(
            new TimeFrameDto(
                ZonedDateTime.of(2012, 3, 22, 0, 0, 0, 0, ZoneId.of("GMT+1")),
                ZonedDateTime.of(2012, 3, 29, 0, 0, 0, 0, ZoneId.of("GMT+1"))
            ),
            disbursementsOverTheWeek
                .stream()
                .map(DisbursementDto::fromDisbursement)
                .collect(Collectors.toUnmodifiableList())
        );
    }
}
