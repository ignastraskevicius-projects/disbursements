package org.ignast.challenge.ecommerce.disbursements.persistence;

import java.time.LocalDate;
import java.util.List;
import org.ignast.challenge.ecommerce.disbursements.domain.DisbursementOverWeekPeriod;
import org.ignast.challenge.ecommerce.disbursements.domain.DisbursementsRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface JpaDisbursementsRepository
    extends DisbursementsRepository, Repository<DisbursementOverWeekPeriod, Integer> {
    @Override
    public List<DisbursementOverWeekPeriod> findByLastDayOfWeekPeriod(final LocalDate date);

    @Override
    @Query(value = "select d from DisbursementOverWeekPeriod d")
    public List<DisbursementOverWeekPeriod> calculateDisbursementsForWeekEndingWith(
        final LocalDate dateLastOfWeek
    );

    public Iterable<DisbursementOverWeekPeriod> saveAll(
        final Iterable<DisbursementOverWeekPeriod> disbursements
    );
}
