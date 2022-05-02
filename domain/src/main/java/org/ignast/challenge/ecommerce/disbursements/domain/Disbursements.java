package org.ignast.challenge.ecommerce.disbursements.domain;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

@Service
public class Disbursements {

    public List<Disbursement> retrieveDisbursementsOverWeekEndingAt(final ZonedDateTime time) {
        return List.of(new Disbursement("amazon@amazon.com", Money.of(new BigDecimal("5.27854324"), "EUR")));
    }
}
