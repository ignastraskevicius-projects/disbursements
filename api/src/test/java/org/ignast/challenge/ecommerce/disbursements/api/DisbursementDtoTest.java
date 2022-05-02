package org.ignast.challenge.ecommerce.disbursements.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import lombok.val;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursement;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class DisbursementDtoTest {

    @Test
    public void test() {
        val disbursement = new Disbursement("microsoftId", Money.of(new BigDecimal("5.432"), "USD"));

        val disbursementDto = DisbursementDto.fromDisbursement(disbursement);

        assertThat(disbursementDto.merchantId()).isEqualTo("microsoftId");
        assertThat(disbursementDto.monetaryValue().amount()).isEqualTo(new BigDecimal("5.432"));
        assertThat(disbursementDto.monetaryValue().currency()).isEqualTo("USD");
    }
}
