package org.ignast.challenge.ecommerce.disbursements.persistence;

import java.time.LocalDate;
import java.util.List;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursement;
import org.ignast.challenge.ecommerce.disbursements.domain.DisbursementsRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaDisbursementsRepository extends DisbursementsRepository {
    @Override
    public List<Disbursement> findByOrderCompletionDateBetween(
        final LocalDate startDate,
        final LocalDate endDate
    );
}
