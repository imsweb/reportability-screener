package com.imsweb;

import org.junit.jupiter.api.Test;

import com.imsweb.ReportabilityScreener.ReportabilityResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Unit test for simple App.
 */
class ReportabilityScreenerTest {

    @Test
    void testReportabilityScreener() {
        ReportabilityScreenerBuilder builder = new ReportabilityScreenerBuilder();
        builder.defaultKeywords();
        ReportabilityScreener screener = builder.build();
        ScreeningResult result = screener.screen("cancer");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.REPORTABLE);
        result = screener.screen("r/o cancer");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.NON_REPORTABLE);
    }

}
