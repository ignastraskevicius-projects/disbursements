package org.ignast.challenge.ecommerce.disbursements.domain;

import java.time.LocalDate;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA requirement for entities
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DisbursementOverWeekPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @NonNull
    @Getter
    private String merchantId;

    @NonNull
    @Getter
    private LocalDate lastDayOfWeekPeriod;

    @NonNull
    @Getter
    @Column(name = "disbursement_amount", precision = 20, scale = 7)
    private Money amount;
}
