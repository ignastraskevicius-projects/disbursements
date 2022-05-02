package org.ignast.challenge.ecommerce.disbursements.api;

import java.math.BigDecimal;

public record MonetaryValueDto(BigDecimal amount, String currency) {}
