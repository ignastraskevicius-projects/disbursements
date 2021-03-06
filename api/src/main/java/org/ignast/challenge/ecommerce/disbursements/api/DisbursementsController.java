package org.ignast.challenge.ecommerce.disbursements.api;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import lombok.val;
import org.ignast.challenge.ecommerce.disbursements.domain.DisbursementOverWeekPeriod;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/disbursements")
@Validated
public class DisbursementsController {

    private final ExecutorService singleThreadExecution = Executors.newSingleThreadExecutor();

    @Autowired
    private Disbursements disbursements;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<Void>> calculateDisbursements(
        @RequestBody @Valid DisbursementsTimeFrame calculation
    ) {
        val result = new DeferredResult<ResponseEntity<Void>>();
        singleThreadExecution.submit(() -> {
            if (
                calculation
                    .timeFrame()
                    .endingBefore()
                    .isAfter(ZonedDateTime.now(ZoneId.of("GMT+1")).toLocalDate())
            ) {
                result.setResult(ResponseEntity.badRequest().build());
            } else {
                disbursements.calculateDisbursementsForWeekEndingBefore(
                    calculation.timeFrame().endingBefore()
                );
                result.setResult(ResponseEntity.accepted().build());
            }
        });
        return result;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public DisbursementsDto retrieveDisbursements(
        @RequestParam(name = "timeFrameEndingBefore") @DateTimeFormat(
            iso = DATE
        ) final LocalDate timeFrameEndingBefore,
        @RequestParam(name = "timeFrameLength") @Pattern(regexp = "1week") final String timeFrameLength
    ) {
        List<DisbursementOverWeekPeriod> disbursementsOverTheWeek = disbursements.retrieveDisbursementsOverWeekEndingBefore(
            timeFrameEndingBefore
        );
        return toDto(timeFrameEndingBefore, disbursementsOverTheWeek);
    }

    private DisbursementsDto toDto(
        LocalDate timeFrameEndingBefore,
        List<DisbursementOverWeekPeriod> disbursementsOverTheWeek
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
