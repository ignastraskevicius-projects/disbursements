package org.ignast.challenge.ecommerce.disbursements.api;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record TimeFrame(@NotNull LocalDate endingBefore, @NotNull @Pattern(regexp = "1week") String length) {}
