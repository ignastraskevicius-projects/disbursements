package org.ignast.challenge.ecommerce.disbursements.domain;

import java.time.LocalDate;
import java.util.List;

public interface DisbursementsRepository {
    public List<DisbursementOverWeekPeriod> findByLastDayOfWeekPeriod(final LocalDate date);

    public List<DisbursementOverWeekPeriod> calculateDisbursementsForWeekEndingWith(
        final LocalDate dateLastOfWeek
    );

    public Iterable<DisbursementOverWeekPeriod> saveAll(
        final Iterable<DisbursementOverWeekPeriod> disbursements
    );
}
