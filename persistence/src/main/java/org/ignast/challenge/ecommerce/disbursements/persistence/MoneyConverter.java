package org.ignast.challenge.ecommerce.disbursements.persistence;

import java.math.BigDecimal;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.javamoney.moneta.Money;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Money money) {
        return money.getNumberStripped();
    }

    @Override
    public Money convertToEntityAttribute(BigDecimal amount) {
        return Money.of(amount, "EUR");
    }
}
