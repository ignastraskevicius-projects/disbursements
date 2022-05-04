package org.ignast.challenge.ecommerce.disbursements.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record DisbursementsTimeFrame(@NotNull @Valid TimeFrame timeFrame) {}
