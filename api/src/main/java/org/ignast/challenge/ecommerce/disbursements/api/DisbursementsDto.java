package org.ignast.challenge.ecommerce.disbursements.api;

import java.util.List;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursement;

public record DisbursementsDto(TimeFrameDto timeFrame, List<DisbursementDto> merchants) {}
