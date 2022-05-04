package org.ignast.challenge.ecommerce.disbursements.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import lombok.val;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class MoneyConverterTest {

    private final MoneyConverter moneyConverter = new MoneyConverter();

    @Test
    public void shouldPersistOnlyAmount() {
        BigDecimal amount = new BigDecimal("5.123");
        assertThat(moneyConverter.convertToDatabaseColumn(Money.of(amount, "EUR"))).isEqualTo(amount);
    }

    @Test
    public void shouldRestoreAsEur() {
        BigDecimal amount = new BigDecimal("5.123");

        val restored = moneyConverter.convertToEntityAttribute(amount);

        assertThat(restored.getCurrency().getCurrencyCode()).isEqualTo("EUR");
        assertThat(restored.getNumberStripped()).isEqualTo(amount);
    }
}
