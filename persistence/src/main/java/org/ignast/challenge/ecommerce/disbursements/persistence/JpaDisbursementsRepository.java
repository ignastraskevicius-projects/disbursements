package org.ignast.challenge.ecommerce.disbursements.persistence;

import java.time.LocalDate;
import java.util.List;
import org.ignast.challenge.ecommerce.disbursements.domain.DisbursementOverWeekPeriod;
import org.ignast.challenge.ecommerce.disbursements.domain.DisbursementsRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface JpaDisbursementsRepository
    extends DisbursementsRepository, Repository<DisbursementOverWeekPeriod, Integer> {
    @Override
    public List<DisbursementOverWeekPeriod> findByLastDayOfWeekPeriod(final LocalDate date);

    @Procedure("calculate_disbursements_over_week_period_ending_on")
    @Override
    public void calculateDisbursementsForWeekEndingWith(final LocalDate dateLastOfWeek);
}
