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
        assertThat(builder.getGroupFromString("Positive")).isEqualTo(POSITIVE);
        assertThat(builder.getGroupFromString("Negative")).isEqualTo(NEGATIVE);
        assertThat(builder.getGroupFromString("Other")).isEqualTo(OTHER);

        assertThatThrownBy(() -> builder.getGroupFromString("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.getGroupFromString("positive")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.getGroupFromString("NEGATIVE")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.getGroupFromString(" Other ")).isInstanceOf(IllegalArgumentException.class);
    }
}
