/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;

import com.imsweb.ReportabilityScreener.Group;

import static com.imsweb.ReportabilityScreener.Group.NEGATIVE;
import static com.imsweb.ReportabilityScreener.Group.OTHER;
import static com.imsweb.ReportabilityScreener.Group.POSITIVE;

public class ReportabilityScreenerBuilder {

    private final TrieBuilder _positiveTrieBuilder;
    private final TrieBuilder _negativeTrieBuilder;
    private final TrieBuilder _otherTrieBuilder;

    public ReportabilityScreenerBuilder() {
        _positiveTrieBuilder = Trie.builder().onlyWholeWords().ignoreCase();
        _negativeTrieBuilder = Trie.builder().onlyWholeWords().ignoreCase();
        _otherTrieBuilder = Trie.builder().onlyWholeWords().ignoreCase();
    }

    public ReportabilityScreener build() {
        return new ReportabilityScreener(_positiveTrieBuilder.build(), _negativeTrieBuilder.build(), _otherTrieBuilder.build());
    }

    public void add(String keyword, Group group) {
        switch (group) {
            case POSITIVE:
                _positiveTrieBuilder.addKeyword(keyword);
                break;
            case NEGATIVE:
                _negativeTrieBuilder.addKeyword(keyword);
                break;
            case OTHER:
                _otherTrieBuilder.addKeyword(keyword);
                break;
        }
    }

    public void add(List<String> keywords, Group group) {
        keywords.forEach(k -> add(k, group));
    }

    public void defaultKeywords() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/default.keyword.list.txt"))) {
            reader.lines().map(l -> l.split("\\|")).forEach(l -> add(l[0], getGroupFromString(l[1])));
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to parse default keyword list.", e);
        }

    }

    protected Group getGroupFromString(String groupString) {
        if (groupString == null || groupString.trim().isEmpty())
            throw new IllegalArgumentException("Group value cannot be blank.");

        Group group;
        switch (groupString.trim().toLowerCase()) {
            case "positive":
            case "pos":
            case "p":
                group = POSITIVE;
                break;
            case "negative":
            case "neg":
            case "n":
                group = NEGATIVE;
                break;
            case "other":
            case "oth":
            case "o":
                group = OTHER;
                break;
            default:
                throw new IllegalArgumentException("Unexpected group value: " + groupString);
        }
        return group;
    }


}
