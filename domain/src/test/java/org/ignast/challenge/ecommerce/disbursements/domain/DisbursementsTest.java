package org.ignast.challenge.ecommerce.disbursements.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
        val expectedDisbursements = List.of(mock(Disbursement.class));
        when(repository.findByLastDayOfWeek(dateLastOfWeek)).thenReturn(expectedDisbursements);

        List<Disbursement> disbursements =
            this.disbursements.retrieveDisbursementsOverWeekEndingBefore(dateAfterLastDay);

        assertThat(disbursements).isSameAs(expectedDisbursements);
    }
}
