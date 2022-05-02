package org.ignast.challenge.ecommerce.disbursements.api;

import org.ignast.challenge.ecommerce.disbursements.domain.Disbursement;

public record DisbursementDto(String merchantId, MonetaryValueDto monetaryValue) {
    public static DisbursementDto fromDisbursement(Disbursement disbursement) {
        return new DisbursementDto(
            disbursement.getMerchantId(),
            new MonetaryValueDto(
                disbursement.getAmount().getNumberStripped(),
                disbursement.getAmount().getCurrency().getCurrencyCode()
            )
        );
    }
}
