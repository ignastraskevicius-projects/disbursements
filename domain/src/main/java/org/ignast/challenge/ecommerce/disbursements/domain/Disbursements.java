package org.ignast.challenge.ecommerce.disbursements.domain;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Disbursements {

    @Autowired
    private final DisbursementsRepository repository;

    public List<Disbursement> retrieveDisbursementsOverWeekEndingBefore(final LocalDate date) {
        val lastDayOfWeek = date.minusDays(1);
        return repository.findByLastDayOfWeek(lastDayOfWeek);
    }
}
