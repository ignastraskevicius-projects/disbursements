package org.ignast.challenge.ecommerce.disbursements.api;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.Pattern;
import lombok.val;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursement;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/disbursements")
@Validated
public class DisbursementsController {

    @Autowired
    private Disbursements disbursements;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public DisbursementsDto retrieveDisbursements(
        @RequestParam(name = "timeFrameEndingBefore") @DateTimeFormat(
            iso = DATE
        ) final LocalDate timeFrameEndingBefore,
        @RequestParam(name = "timeFrame") @Pattern(regexp = "1week") final String timeFrame
    ) {
        List<Disbursement> disbursementsOverTheWeek = disbursements.retrieveDisbursementsOverWeekEndingBefore(
            timeFrameEndingBefore
        );
        return toDto(timeFrameEndingBefore, disbursementsOverTheWeek);
    }

    private DisbursementsDto toDto(
        LocalDate timeFrameEndingBefore,
        List<Disbursement> disbursementsOverTheWeek
    ) {
        val endTime = timeFrameEndingBefore.atStartOfDay(ZoneId.of("GMT+1"));
        val startTime = endTime.minusDays(7);
        return new DisbursementsDto(
            new TimeFrameDto(startTime, endTime),
            disbursementsOverTheWeek
                .stream()
                .map(DisbursementDto::fromDisbursement)
                .collect(Collectors.toUnmodifiableList())
        );
    }
}
