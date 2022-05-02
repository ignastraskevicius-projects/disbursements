package org.ignast.challenge.ecommerce.disbursements.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA requirement for entities
public class Disbursement {

    @NonNull
    @Getter
    private String merchantId;

    @NonNull
    @Getter
    private Money amount;
}
