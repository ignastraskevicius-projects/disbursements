package org.ignast.challenge.ecommerce.disbursements.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Repository;

@Repository
public class StubDisbursementsRepository implements DisbursementsRepository {

    @Override
    public List<Disbursement> findByLastDayOfWeek(LocalDate date) {
        return List.of(new Disbursement("amazon@amazon.com", Money.of(new BigDecimal("5.27854324"), "EUR")));
    }
}
