package com.imsweb;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.imsweb.ReportabilityScreener.Group;
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
        result = screener.screen("not cancer");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.NON_REPORTABLE);
        result = screener.screen("not cancer cancer");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.REPORTABLE);

        builder = new ReportabilityScreenerBuilder();
        builder.add("cancer", Group.POSITIVE);
        builder.add("not cancer", Group.NEGATIVE);
        builder.add("other", Group.OTHER);
        screener = builder.build();
        result = screener.screen("cancer");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.REPORTABLE);
        result = screener.screen("not cancer");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.NON_REPORTABLE);
        result = screener.screen("not cancer other cancer");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.REPORTABLE);

        builder = new ReportabilityScreenerBuilder();
        builder.add(Arrays.asList("cancer", "malignant neoplasm", "ca"), Group.POSITIVE);
        builder.add(Arrays.asList("not cancer", "r/o cancer", "no ca"), Group.NEGATIVE);
        builder.add(Arrays.asList("other", "sella turcica"), Group.OTHER);
        screener = builder.build();
        result = screener.screen("cancer");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.REPORTABLE);
        result = screener.screen("not cancer");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.NON_REPORTABLE);
        // positive keyword "ca" should not match in "turcica" because keyword matches are whole-word-only
        result = screener.screen("not cancer no ca sella turcica");
        assertThat(result.getResult()).isEqualTo(ReportabilityResult.NON_REPORTABLE);
    }

    @Test
    void testIgnoreNegatedPositiveKeywordMatches() {
        List<Keyword> positiveKeywordMatches;
        List<Keyword> negativeKeywordMatches;
        ReportabilityScreener screener = new ReportabilityScreenerBuilder().build();

        Keyword posKeyword10to20 = new Keyword("posKeyword", 10, 20, Group.POSITIVE);
        Keyword posKeyword15to25 = new Keyword("posKeyword", 15, 25, Group.POSITIVE);
        Keyword posKeyword15to16 = new Keyword("posKeyword", 15, 16, Group.POSITIVE);
        Keyword negKeyword9to21 = new Keyword("negKeyword", 9, 21, Group.NEGATIVE);
        Keyword negKeyword10to20 = new Keyword("negKeyword", 10, 20, Group.NEGATIVE);
        Keyword negKeyword15to16 = new Keyword("negKeyword", 15, 16, Group.NEGATIVE);

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
        assertThat(ReportabilityResult.NON_REPORTABLE).isEqualTo(screener.getResultBasedOnKeywordMatches(Collections.emptyList()));

        Keyword positiveKeywordMatch = new Keyword("positive keyword", 1, 2, Group.POSITIVE);
        Keyword ignoredPositiveMatch = new Keyword("positive keyword", 3, 4, Group.POSITIVE);
        ignoredPositiveMatch.setIgnored(true);

        assertThat(ReportabilityResult.REPORTABLE).isEqualTo(screener.getResultBasedOnKeywordMatches(Collections.singletonList(positiveKeywordMatch)));
        assertThat(ReportabilityResult.NON_REPORTABLE).isEqualTo(screener.getResultBasedOnKeywordMatches(Collections.singletonList(ignoredPositiveMatch)));
        assertThat(ReportabilityResult.REPORTABLE).isEqualTo(screener.getResultBasedOnKeywordMatches(Arrays.asList(positiveKeywordMatch, ignoredPositiveMatch)));
    }
}
