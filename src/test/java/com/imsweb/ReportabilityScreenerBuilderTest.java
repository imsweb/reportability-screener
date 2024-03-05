/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb;

import org.junit.jupiter.api.Test;

import static com.imsweb.ReportabilityScreener.Group.NEGATIVE;
import static com.imsweb.ReportabilityScreener.Group.OTHER;
import static com.imsweb.ReportabilityScreener.Group.POSITIVE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ReportabilityScreenerBuilderTest {

    @Test
    void testGetGroupFromString() {
        ReportabilityScreenerBuilder builder = new ReportabilityScreenerBuilder();
        assertThat(builder.getGroupFromString("POSITIVE")).isEqualTo(POSITIVE);
        assertThat(builder.getGroupFromString("Negative")).isEqualTo(NEGATIVE);
        assertThat(builder.getGroupFromString("other")).isEqualTo(OTHER);

        assertThat(builder.getGroupFromString(" POS ")).isEqualTo(POSITIVE);
        assertThat(builder.getGroupFromString("\tNeg\t")).isEqualTo(NEGATIVE);
        assertThat(builder.getGroupFromString("\noth\r\n")).isEqualTo(OTHER);

        assertThat(builder.getGroupFromString("P")).isEqualTo(POSITIVE);
        assertThat(builder.getGroupFromString("n")).isEqualTo(NEGATIVE);
        assertThat(builder.getGroupFromString("o")).isEqualTo(OTHER);

        assertThatThrownBy(() -> builder.getGroupFromString(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.getGroupFromString("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.getGroupFromString("    ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.getGroupFromString("+")).isInstanceOf(IllegalArgumentException.class);
    }
}
