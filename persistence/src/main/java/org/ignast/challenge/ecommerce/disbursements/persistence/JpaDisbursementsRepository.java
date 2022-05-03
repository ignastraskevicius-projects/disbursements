package org.ignast.challenge.ecommerce.disbursements.persistence;

import java.time.LocalDate;
import java.util.List;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursement;
import org.ignast.challenge.ecommerce.disbursements.domain.DisbursementsRepository;

public interface JpaDisbursementsRepository extends DisbursementsRepository {
    @Override
    public List<Disbursement> findByLastDayOfWeek(final LocalDate date);
}
