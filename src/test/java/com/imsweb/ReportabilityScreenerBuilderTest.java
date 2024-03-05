/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import static com.imsweb.ReportabilityScreener.Group.NEGATIVE;
import static com.imsweb.ReportabilityScreener.Group.OTHER;
import static com.imsweb.ReportabilityScreener.Group.POSITIVE;
import static com.imsweb.ReportabilityScreenerBuilder._KEYWORD_MAX_LENGTH;
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

    @Test
    public void testFormatKeyword() {
        String longStringUpper = "    " + String.join("", Collections.nCopies(_KEYWORD_MAX_LENGTH, "A")) + "    ";
        String longStringLower = String.join("", Collections.nCopies(_KEYWORD_MAX_LENGTH, "a"));
        String tooLong = String.join("", Collections.nCopies(_KEYWORD_MAX_LENGTH + 1, "A"));

        ReportabilityScreenerBuilder builder = new ReportabilityScreenerBuilder();
        assertThat(builder.formatKeyword("keyword1")).isEqualTo("keyword1");
        assertThat(builder.formatKeyword("KEYWORD2")).isEqualTo("keyword2");
        assertThat(builder.formatKeyword("    KEYWORD3    ")).isEqualTo("keyword3");
        assertThat(builder.formatKeyword(longStringUpper)).isEqualTo(longStringLower);

        assertThatThrownBy(() -> builder.formatKeyword(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.formatKeyword("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.formatKeyword("    ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.formatKeyword(tooLong)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.formatKeyword("keyword1")).isInstanceOf(IllegalArgumentException.class);
    }
}
