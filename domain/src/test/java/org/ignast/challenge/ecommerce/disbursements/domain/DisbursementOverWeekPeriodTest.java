package org.ignast.challenge.ecommerce.disbursements.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDate;
import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class DisbursementOverWeekPeriodTest {

    private static final LocalDate DATE = LocalDate.of(2022, 1, 1);

    private static final Money MONEY = Money.of(5, "EUR");

    private static final String MERCHANT = "amazon@amazon.com";

    @Test
    public void shouldEqualToAnotherDisbursement() {
        EqualsVerifier.forClass(DisbursementOverWeekPeriod.class).suppress(Warning.SURROGATE_KEY).verify();
    }

    @Test
    public void shouldContainFields() {
        val disbursement = new DisbursementOverWeekPeriod(MERCHANT, DATE, MONEY);

        assertThat(disbursement.getExternalMerchantId()).isEqualTo(MERCHANT);
        assertThat(disbursement.getLastDayOfWeekPeriod()).isEqualTo(DATE);
        assertThat(disbursement.getAmount()).isEqualTo(MONEY);
    }

    @Test
    public void shouldNotHaveNullFields() {
        assertThatNullPointerException()
            .isThrownBy(() -> new DisbursementOverWeekPeriod(null, anyDate(), anyMoney()));
        assertThatNullPointerException()
            .isThrownBy(() -> new DisbursementOverWeekPeriod("anyId", null, anyMoney()));
        assertThatNullPointerException()
            .isThrownBy(() -> new DisbursementOverWeekPeriod("anyId", anyDate(), null));
    }

    private Money anyMoney() {
        return Money.of(5, "EUR");
    }

    private LocalDate anyDate() {
        return LocalDate.of(2022, 1, 1);
    }
}
