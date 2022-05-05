package org.ignast.challenge.ecommerce.disbursements.api;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.val;
import org.ignast.challenge.ecommerce.disbursements.domain.DisbursementOverWeekPeriod;
import org.ignast.challenge.ecommerce.disbursements.domain.Disbursements;
import org.ignast.challenge.ecommerce.disbursements.domain.NoDisbursementsFound;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest
class DisbursementsControllerTest {

    private static final LocalDate FIRST_MONDAY_OF_2022 = LocalDate.of(2022, 1, 3);
    private static final String FIRST_MONDAY_OF_2022_TEXT = "2022-01-03";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Disbursements disbursements;

    @Test
    public void shouldSerializeDisbursements() throws Exception {
        when(disbursements.retrieveDisbursementsOverWeekEndingBefore(FIRST_MONDAY_OF_2022))
            .thenReturn(
                List.of(
                    new DisbursementOverWeekPeriod(
                        "amazon@amazon.com",
                        anyDate(),
                        Money.of(new BigDecimal("5.27854324"), "EUR")
                    )
                )
            );
        mockMvc
            .perform(
                get(
                    "/disbursements?timeFrameEndingBefore=" +
                    FIRST_MONDAY_OF_2022_TEXT +
                    "&timeFrameLength=1week"
                )
                    .accept(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
            .andExpect(content().json(amazon()));
    }

    private LocalDate anyDate() {
        return LocalDate.of(2022, 1, 1);
    }

    @Test
    public void noDisbursementsFoundShouldMeanThatHeyAreNotCalculatedYet() throws Exception {
        when(disbursements.retrieveDisbursementsOverWeekEndingBefore(FIRST_MONDAY_OF_2022))
            .thenThrow(NoDisbursementsFound.class);

        mockMvc
            .perform(
                get(
                    "/disbursements?timeFrameEndingBefore=" +
                    FIRST_MONDAY_OF_2022_TEXT +
                    "&timeFrameLength=1week"
                )
                    .accept(APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAcceptNonWeekTimeframes() throws Exception {
        mockMvc
            .perform(
                get(
                    "/disbursements?timeFrameEndingBefore=" +
                    FIRST_MONDAY_OF_2022_TEXT +
                    "&timeFrameLength=1month"
                )
                    .accept(APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldAcceptOnly1WeekTimeframes() throws Exception {
        mockMvc
            .perform(
                get(
                    "/disbursements?timeFrameEndingBefore=" +
                    FIRST_MONDAY_OF_2022_TEXT +
                    "&timeFrameLength=2week"
                )
                    .accept(APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldAcceptDisbursementCreation() throws Exception {
        val result = mockMvc
            .perform(
                post("/disbursements")
                    .contentType(APPLICATION_JSON)
                    .content(
                        format(
                            """
                            {"timeFrame":{
                                "length":"1week",
                                "endingBefore":"%s"
                            }}""",
                            FIRST_MONDAY_OF_2022_TEXT
                        )
                    )
            )
            .andReturn();

        mockMvc.perform(asyncDispatch(result)).andExpect(status().isAccepted());

        verify(disbursements).calculateDisbursementsForWeekEndingBefore(FIRST_MONDAY_OF_2022);
    }

    @Test
    public void shouldNotAcceptDisbursementCreationWithoutTimeFrame() throws Exception {
        mockMvc
            .perform(
                post("/disbursements")
                    .contentType(APPLICATION_JSON)
                    .content(format("""
                            {}""", FIRST_MONDAY_OF_2022_TEXT))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAcceptDisbursementCreationWithNon1WeekTimeFrames() throws Exception {
        mockMvc
            .perform(
                post("/disbursements")
                    .contentType(APPLICATION_JSON)
                    .content(
                        format(
                            """
                            {"timeFrame":{
                                "length":"2week",
                                "endingBefore":"%s"
                            }}""",
                            FIRST_MONDAY_OF_2022_TEXT
                        )
                    )
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAcceptDisbursementCreationWithoutTimeFrameLength() throws Exception {
        mockMvc
            .perform(
                post("/disbursements")
                    .contentType(APPLICATION_JSON)
                    .content(
                        format(
                            """
                            {"timeFrame":{
                                "endingBefore":"%s"
                            }}""",
                            FIRST_MONDAY_OF_2022_TEXT
                        )
                    )
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAcceptDisbursementCreationWithoutTimeFrameDate() throws Exception {
        mockMvc
            .perform(
                post("/disbursements")
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                            {"timeFrame":{
                                "length":"1week"
                            }}"""
                    )
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAcceptDisbursementCreationWithFutureDate() throws Exception {
        val tomorrow = ZonedDateTime.now(ZoneId.of("GMT+1")).plusDays(1).toLocalDate().toString();
        val result = mockMvc
            .perform(
                post("/disbursements")
                    .contentType(APPLICATION_JSON)
                    .content(
                        format(
                            """
                            {"timeFrame":{
                                "length":"1week",
                                "endingBefore":"%s"
                            }}""",
                            tomorrow
                        )
                    )
            )
            .andReturn();

        mockMvc.perform(asyncDispatch(result)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldAcceptDisbursementCreationForToday() throws Exception {
        val today = ZonedDateTime.now(ZoneId.of("GMT+1")).toLocalDate().toString();

        val result = mockMvc
            .perform(
                post("/disbursements")
                    .contentType(APPLICATION_JSON)
                    .content(
                        format(
                            """
                            {"timeFrame":{
                                "length":"1week",
                                "endingBefore":"%s"
                            }}""",
                            today
                        )
                    )
            )
            .andReturn();

        mockMvc.perform(asyncDispatch(result)).andExpect(status().isAccepted());
    }

    private final String amazon() {
        return """
                {
                    "timeFrame":{
                        "start":"2021-12-27T00:00:00+01:00",
                        "end":"2022-01-03T00:00:00+01:00"
                    },
                    "merchants":[{
                        "merchantId":"amazon@amazon.com",
                        "monetaryValue":{
                            "amount":5.27854324,
                            "currency":"EUR"
                        }
                    }]
                }""";
    }
}
