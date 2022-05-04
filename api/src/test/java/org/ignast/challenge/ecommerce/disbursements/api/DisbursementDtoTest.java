package org.ignast.challenge.ecommerce.disbursements.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.val;
import org.ignast.challenge.ecommerce.disbursements.domain.DisbursementOverWeekPeriod;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class DisbursementDtoTest {

    @Test
    public void test() {
        val disbursement = new DisbursementOverWeekPeriod(
            "microsoftId",
            anyDate(),
            Money.of(new BigDecimal("5.432"), "USD")
        );

        val disbursementDto = DisbursementDto.fromDisbursement(disbursement);

        assertThat(disbursementDto.merchantId()).isEqualTo("microsoftId");
        assertThat(disbursementDto.monetaryValue().amount()).isEqualTo(new BigDecimal("5.432"));
        assertThat(disbursementDto.monetaryValue().currency()).isEqualTo("USD");
    }

    private LocalDate anyDate() {
        return LocalDate.of(2022, 1, 1);
    }
}
