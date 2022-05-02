package org.ignast.challenge.ecommerce.disbursements.api;

import java.time.ZonedDateTime;

public record TimeFrameDto(ZonedDateTime start, ZonedDateTime end) {}
