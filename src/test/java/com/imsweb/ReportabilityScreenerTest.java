package com.imsweb;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Unit test for simple App.
 */
class ReportabilityScreenerTest {

    @Test
    void testGetReportability() {
        assertThat(new ReportabilityScreener().getReportability()).isEqualTo("test");
    }

}
