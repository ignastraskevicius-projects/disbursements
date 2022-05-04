package org.ignast.challenge.ecommerce.disbursements.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Repository;

@Repository
public class StubDisbursementsRepository implements DisbursementsRepository {

    @Override
    public List<DisbursementOverWeekPeriod> findByLastDayOfWeekPeriod(final LocalDate date) {
        return List.of(
            new DisbursementOverWeekPeriod(
                "amazon@amazon.com",
                LocalDate.of(2022, 1, 1),
                Money.of(new BigDecimal("5.27854324"), "EUR")
            )
        );
    }
}
