package com.imsweb;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static com.imsweb.ReportabilityScreener.Group.NEGATIVE;
import static com.imsweb.ReportabilityScreener.Group.OTHER;
import static com.imsweb.ReportabilityScreener.Group.POSITIVE;
import static com.imsweb.ReportabilityScreener.ReportabilityResult.NON_REPORTABLE;
import static com.imsweb.ReportabilityScreener.ReportabilityResult.REPORTABLE;
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
        assertThat(result.getResult()).isEqualTo(REPORTABLE);
        result = screener.screen("not cancer");
        assertThat(result.getResult()).isEqualTo(NON_REPORTABLE);
        result = screener.screen("not cancer cancer");
        assertThat(result.getResult()).isEqualTo(REPORTABLE);

        builder = new ReportabilityScreenerBuilder();
        builder.add("cancer", POSITIVE);
        builder.add("not cancer", NEGATIVE);
        builder.add("other", OTHER);
        screener = builder.build();
        result = screener.screen("cancer");
        assertThat(result.getResult()).isEqualTo(REPORTABLE);
        result = screener.screen("not cancer");
        assertThat(result.getResult()).isEqualTo(NON_REPORTABLE);
        result = screener.screen("not cancer other cancer");
        assertThat(result.getResult()).isEqualTo(REPORTABLE);

        builder = new ReportabilityScreenerBuilder();
        builder.add(Arrays.asList("cancer", "malignant neoplasm", "ca"), POSITIVE);
        builder.add(Arrays.asList("not cancer", "r/o cancer", "no ca"), NEGATIVE);
        builder.add(Arrays.asList("other", "sella turcica"), OTHER);
        screener = builder.build();
        result = screener.screen("cancer");
        assertThat(result.getResult()).isEqualTo(REPORTABLE);
        result = screener.screen("not cancer");
        assertThat(result.getResult()).isEqualTo(NON_REPORTABLE);
        // positive keyword "ca" should not match in "turcica" because keyword matches are whole-word-only
        result = screener.screen("not cancer no ca sella turcica");
        assertThat(result.getResult()).isEqualTo(NON_REPORTABLE);
    }

    @Test
    void testIgnoreNegatedPositiveKeywordMatches() {
        List<Keyword> positiveKeywordMatches;
        List<Keyword> negativeKeywordMatches;
        ReportabilityScreener screener = new ReportabilityScreenerBuilder().build();

        Keyword posKeyword10to20 = new Keyword("posKeyword", 10, 20, POSITIVE);
        Keyword posKeyword15to25 = new Keyword("posKeyword", 15, 25, POSITIVE);
        Keyword posKeyword15to16 = new Keyword("posKeyword", 15, 16, POSITIVE);
        Keyword negKeyword9to21 = new Keyword("negKeyword", 9, 21, NEGATIVE);
        Keyword negKeyword10to20 = new Keyword("negKeyword", 10, 20, NEGATIVE);
        Keyword negKeyword15to16 = new Keyword("negKeyword", 15, 16, NEGATIVE);

        // single positive keyword match -> not ignored
        positiveKeywordMatches = Collections.singletonList(posKeyword10to20);
        negativeKeywordMatches = Collections.emptyList();
        screener.ignoreNegatedPositiveKeywordMatches(positiveKeywordMatches, negativeKeywordMatches);
        assertThat(posKeyword10to20.isIgnored()).isFalse();

        //single negative keyword match -> not ignored
        positiveKeywordMatches = Collections.emptyList();
        negativeKeywordMatches = Collections.singletonList(negKeyword9to21);
        screener.ignoreNegatedPositiveKeywordMatches(positiveKeywordMatches, negativeKeywordMatches);
        assertThat(negKeyword9to21.isIgnored()).isFalse();

        // positive keyword overlapped by negative keyword -> positive keyword ignored
        positiveKeywordMatches = Collections.singletonList(posKeyword10to20);
        negativeKeywordMatches = Collections.singletonList(negKeyword9to21);
        screener.ignoreNegatedPositiveKeywordMatches(positiveKeywordMatches, negativeKeywordMatches);
        assertThat(posKeyword10to20.isIgnored()).isTrue();
        assertThat(negKeyword9to21.isIgnored()).isFalse();
        posKeyword10to20.setIgnored(false);

        // test match boundary constraints -> positive keyword ignored
        positiveKeywordMatches = Collections.singletonList(posKeyword10to20);
        negativeKeywordMatches = Collections.singletonList(negKeyword10to20);
        screener.ignoreNegatedPositiveKeywordMatches(positiveKeywordMatches, negativeKeywordMatches);
        assertThat(posKeyword10to20.isIgnored()).isTrue();
        assertThat(negKeyword10to20.isIgnored()).isFalse();
        posKeyword10to20.setIgnored(false);

        // positive keyword overlapped by positive keyword -> not ignored
        positiveKeywordMatches = Arrays.asList(posKeyword10to20, posKeyword15to16);
        negativeKeywordMatches = Collections.emptyList();
        screener.ignoreNegatedPositiveKeywordMatches(positiveKeywordMatches, negativeKeywordMatches);
        assertThat(posKeyword10to20.isIgnored()).isFalse();
        assertThat(posKeyword15to16.isIgnored()).isFalse();

        // positive keyword not completely overlapped by negative keyword -> not ignored
        positiveKeywordMatches = Collections.singletonList(posKeyword15to25);
        negativeKeywordMatches = Collections.singletonList(negKeyword10to20);
        screener.ignoreNegatedPositiveKeywordMatches(positiveKeywordMatches, negativeKeywordMatches);
        assertThat(posKeyword15to25.isIgnored()).isFalse();

        // two positive keywords overlapped by the same negative keyword -> both ignored
        positiveKeywordMatches = Arrays.asList(posKeyword10to20, posKeyword15to16);
        negativeKeywordMatches = Collections.singletonList(negKeyword9to21);
        screener.ignoreNegatedPositiveKeywordMatches(positiveKeywordMatches, negativeKeywordMatches);
        assertThat(posKeyword10to20.isIgnored()).isTrue();
        assertThat(posKeyword15to16.isIgnored()).isTrue();
        assertThat(negKeyword9to21.isIgnored()).isFalse();
        posKeyword10to20.setIgnored(false);
        posKeyword15to16.setIgnored(false);

        // negative keyword overlapped by positive keyword -> not ignored
        positiveKeywordMatches = Collections.singletonList(posKeyword10to20);
        negativeKeywordMatches = Collections.singletonList(negKeyword15to16);
        screener.ignoreNegatedPositiveKeywordMatches(positiveKeywordMatches, negativeKeywordMatches);
        assertThat(posKeyword10to20.isIgnored()).isFalse();
        assertThat(negKeyword15to16.isIgnored()).isFalse();
    }

    @Test
    void testGetResultBasedOnKeywordMatches() {
        ReportabilityScreener screener = new ReportabilityScreenerBuilder().build();
        assertThat(NON_REPORTABLE).isEqualTo(screener.getResultBasedOnKeywordMatches(Collections.emptyList()));

        Keyword positiveKeywordMatch = new Keyword("positive keyword", 1, 2, POSITIVE);
        Keyword ignoredPositiveMatch = new Keyword("positive keyword", 3, 4, POSITIVE);
        ignoredPositiveMatch.setIgnored(true);

        assertThat(REPORTABLE).isEqualTo(screener.getResultBasedOnKeywordMatches(Collections.singletonList(positiveKeywordMatch)));
        assertThat(NON_REPORTABLE).isEqualTo(screener.getResultBasedOnKeywordMatches(Collections.singletonList(ignoredPositiveMatch)));
        assertThat(REPORTABLE).isEqualTo(screener.getResultBasedOnKeywordMatches(Arrays.asList(positiveKeywordMatch, ignoredPositiveMatch)));
    }
}
