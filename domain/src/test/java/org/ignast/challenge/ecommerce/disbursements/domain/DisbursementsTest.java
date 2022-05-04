package org.ignast.challenge.ecommerce.disbursements.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;

class DisbursementsTest {

    private final DisbursementsRepository repository = mock(DisbursementsRepository.class);

    private final Disbursements disbursements = new Disbursements(repository);

    @Test
    public void shouldQueryRepository() {
        val dateAfterLastDay = LocalDate.of(2022, 1, 8);
        val dateLastOfWeek = LocalDate.of(2022, 1, 7);
        val expectedDisbursements = List.of(mock(DisbursementOverWeekPeriod.class));
        when(repository.findByLastDayOfWeekPeriod(dateLastOfWeek)).thenReturn(expectedDisbursements);

        List<DisbursementOverWeekPeriod> disbursements =
            this.disbursements.retrieveDisbursementsOverWeekEndingBefore(dateAfterLastDay);

        assertThat(disbursements).isSameAs(expectedDisbursements);
    }

    @Test
    public void noDisbursementsForThePeriodShouldMeanThatTheyWereNotCalculatedYet() {
        when(repository.findByLastDayOfWeekPeriod(any())).thenReturn(List.of());

        assertThatExceptionOfType(NoDisbursementsFound.class)
            .isThrownBy(() -> this.disbursements.retrieveDisbursementsOverWeekEndingBefore(anyDate()));
    }

    @Test
    public void shouldCalculateDisbursementsForWeekBeforeGivenDate() {
        val dateAfterLastDay = LocalDate.of(2022, 1, 8);
        val dateLastOfWeek = LocalDate.of(2022, 1, 7);
        val expectedDisbursements = List.of(mock(DisbursementOverWeekPeriod.class));
        when(repository.calculateDisbursementsForWeekEndingWith(dateLastOfWeek))
            .thenReturn(expectedDisbursements);

        disbursements.calculateDisbursementsForWeekEndingBefore(dateAfterLastDay);

        verify(repository).saveAll(expectedDisbursements);
    }

    private LocalDate anyDate() {
        return LocalDate.of(2022, 1, 8);
    }
}
