package com.imsweb;

import java.util.List;
import java.util.stream.Collectors;

import org.ahocorasick.trie.Trie;

import static com.imsweb.ReportabilityScreener.Group.NEGATIVE;
import static com.imsweb.ReportabilityScreener.Group.OTHER;
import static com.imsweb.ReportabilityScreener.Group.POSITIVE;
import static com.imsweb.ReportabilityScreener.ReportabilityResult.NON_REPORTABLE;
import static com.imsweb.ReportabilityScreener.ReportabilityResult.REPORTABLE;

public class ReportabilityScreener {

    public enum Group {POSITIVE, NEGATIVE, OTHER}

    public enum ReportabilityResult {REPORTABLE, NON_REPORTABLE, UNKNOWN}

    private final Trie _positiveTrie;
    private final Trie _negativeTrie;
    private final Trie _otherTrie;

    ReportabilityScreener(Trie positiveTrie, Trie negativeTrie, Trie otherTrie) {
        _positiveTrie = positiveTrie;
        _negativeTrie = negativeTrie;
        _otherTrie = otherTrie;
    }

    /**
     * Screen text and return information about reportability and keywords found
     * @param text text to screen
     * @return a {@link ScreeningResult} which includes reportability as well as the keywords used to determine it
     */
    public ScreeningResult screen(String text) {
        ScreeningResult result = new ScreeningResult();

        result.setPositiveKeywords(_positiveTrie.parseText(text).stream().map(e -> new Keyword(e, POSITIVE)).collect(Collectors.toList()));
        result.setNegativeKeywords(_negativeTrie.parseText(text).stream().map(e -> new Keyword(e, NEGATIVE)).collect(Collectors.toList()));
        result.setOtherKeywords(_otherTrie.parseText(text).stream().map(e -> new Keyword(e, OTHER)).collect(Collectors.toList()));

        ignoreNegatedPositiveKeywordMatches(result.getPositiveKeywords(), result.getNegativeKeywords());
        result.setResult(getResultBasedOnKeywordMatches(result.getPositiveKeywords()));

        return result;
    }

    /**
     * Returns true if the text screens as reportable
     * @param text text to screen
     * @return true if reportable
     */
    public boolean isReportable(String text) {
        return screen(text).getResult().equals(REPORTABLE);
    }

    /**
     * If the start/end indexes of a positive keyword are withing the start/end indexes of a negative keyword,
     * set ignored=true on the positive keyword.
     * <p />
     * E.g. the text "not cancer" will create keyword matches for positive keyword "cancer" and
     * negative keyword "not cancer". The positive keyword is being negated, and should be ignored.
     * @param positiveKeywords positive keyword matches from the text being screened, including start/end indexes
     * @param negativeKeywords negative keyword matches from the text being screened, including start/end indexes
     */
    protected void ignoreNegatedPositiveKeywordMatches(List<Keyword> positiveKeywords, List<Keyword> negativeKeywords) {
        positiveKeywords.stream()
                .filter(k -> negativeKeywords.stream().anyMatch(nk -> nk.getStart() <= k.getStart() && k.getEnd() <= nk.getEnd()))
                .forEach(k -> k.setIgnored(true));
    }

    /**
     * If there are any non-ignored positive keyword matches, the screened text is considered REPORTABLE.
     * Otherwise, it is considered NON_REPORTABLE.
     * @param positiveKeywords positive keyword matches from the text being screened
     * @return ReportabilityResult indicating whether the screened text is REPORTABLE or NON_REPORTABLE.
     */
    protected ReportabilityResult getResultBasedOnKeywordMatches(List<Keyword> positiveKeywords) {
        if (positiveKeywords.stream().anyMatch(k -> !k.isIgnored()))
            return REPORTABLE;
        else
            return NON_REPORTABLE;
    }
}
