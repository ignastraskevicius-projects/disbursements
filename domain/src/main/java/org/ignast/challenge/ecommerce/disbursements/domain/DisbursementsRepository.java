package org.ignast.challenge.ecommerce.disbursements.domain;

import java.time.LocalDate;
import java.util.List;

public interface DisbursementsRepository {
    public List<Disbursement> findByOrderCompletionDateBetween(
        final LocalDate startDate,
        final LocalDate endTime
    );
}
