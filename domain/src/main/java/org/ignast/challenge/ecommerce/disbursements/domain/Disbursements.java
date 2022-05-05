package org.ignast.challenge.ecommerce.disbursements.domain;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@NamedStoredProcedureQuery(
    name = "calculateDisbursements",
    procedureName = "calculate_disbursements_over_week_period_ending_on",
    parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "last_day", type = LocalDate.class),
    }
)
public class Disbursements {

    @Autowired
    private final DisbursementsRepository repository;

    public List<DisbursementOverWeekPeriod> retrieveDisbursementsOverWeekEndingBefore(final LocalDate date) {
        val lastDayOfWeek = date.minusDays(1);

        List<DisbursementOverWeekPeriod> disbursemenetsForTheWeek = repository.findByLastDayOfWeekPeriod(
            lastDayOfWeek
        );
        if (disbursemenetsForTheWeek.isEmpty()) {
            throw new NoDisbursementsFound();
        }
        return disbursemenetsForTheWeek;
    }

    public void calculateDisbursementsForWeekEndingBefore(final LocalDate date) {
        val lastDayOfWeek = date.minusDays(1);
        repository.calculateDisbursementsForWeekEndingWith(lastDayOfWeek);
    }
}
