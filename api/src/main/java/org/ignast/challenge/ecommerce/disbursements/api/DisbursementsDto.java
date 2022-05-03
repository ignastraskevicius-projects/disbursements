package org.ignast.challenge.ecommerce.disbursements.api;

import java.util.List;

public record DisbursementsDto(TimeFrameDto timeFrame, List<DisbursementDto> merchants) {}
