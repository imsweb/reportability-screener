package com.imsweb;

import java.util.List;
import java.util.stream.Collectors;

import org.ahocorasick.trie.Trie;

public class ReportabilityScreener {

    public enum Group {POSITIVE, NEGATIVE, OTHER}
    public enum ReportabilityResult {REPORTABLE, NON_REPORTABLE, UNKNOWN}

    private final Trie _positiveTrie;
    private final Trie _negativeTrie;
    private final Trie _otherTrie;

    public ReportabilityScreener(Trie positiveTrie, Trie negativeTrie, Trie otherTrie) {
        _positiveTrie = positiveTrie;
        _negativeTrie = negativeTrie;
        _otherTrie = otherTrie;
    }

    public ScreeningResult screen(String text) {
        ScreeningResult result = new ScreeningResult();

        result.setPositiveKeywords(_positiveTrie.parseText(text).stream().map(e -> new Keyword(e, Group.POSITIVE)).collect(Collectors.toList()));
        result.setNegativeKeywords(_negativeTrie.parseText(text).stream().map(e -> new Keyword(e, Group.NEGATIVE)).collect(Collectors.toList()));
        result.setOtherKeywords(_otherTrie.parseText(text).stream().map(e -> new Keyword(e, Group.OTHER)).collect(Collectors.toList()));

        ignoreNegatedPositiveKeywordMatches(result.getPositiveKeywords(), result.getNegativeKeywords());
        result.setResult(getResultBasedOnKeywordMatches(result.getPositiveKeywords()));

        return result;
    }

    private void ignoreNegatedPositiveKeywordMatches(List<Keyword> positiveKeywords, List<Keyword> negativeKeywords) {
        positiveKeywords.stream()
                .filter(k -> negativeKeywords.stream().anyMatch(nk -> nk.getStart() <= k.getStart() && k.getEnd() <= nk.getEnd()))
                .forEach(k -> k.setIgnored(true));
    }

    private ReportabilityResult getResultBasedOnKeywordMatches(List<Keyword> positiveKeywords) {
        if (positiveKeywords.stream().anyMatch(k -> !k.isIgnored()))
            return ReportabilityResult.REPORTABLE;
        else
            return ReportabilityResult.NON_REPORTABLE;
    }
}
