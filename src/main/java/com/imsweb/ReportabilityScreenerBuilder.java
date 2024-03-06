/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

    private final List<String> _keywords;

    protected static final int _KEYWORD_MAX_LENGTH = 200;

    public ReportabilityScreenerBuilder() {
        _positiveTrieBuilder = Trie.builder().onlyWholeWords().ignoreCase();
        _negativeTrieBuilder = Trie.builder().onlyWholeWords().ignoreCase();
        _otherTrieBuilder = Trie.builder().onlyWholeWords().ignoreCase();
        _keywords = new ArrayList<>();
    }

    public ReportabilityScreener build() {
        return new ReportabilityScreener(_positiveTrieBuilder.build(), _negativeTrieBuilder.build(), _otherTrieBuilder.build());
    }

    public ReportabilityScreenerBuilder add(String keyword, Group group) {
        keyword = formatKeyword(keyword);
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

        return this;
    }

    public ReportabilityScreenerBuilder add(List<String> keywords, Group group) {
        keywords.forEach(k -> add(k, group));

        return this;
    }

    public ReportabilityScreenerBuilder defaultKeywords() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/default.keyword.list.txt"))) {
            reader.lines().map(l -> l.split("\\|")).forEach(l -> add(l[0], getGroupFromString(l[1])));
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to parse default keyword list.", e);
        }

        return this;
    }

    protected String formatKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty())
            throw new IllegalArgumentException("Keyword cannot be blank.");
        keyword = keyword.trim().toLowerCase();
        if (keyword.length() > _KEYWORD_MAX_LENGTH)
            throw new IllegalArgumentException("Keyword must be " + _KEYWORD_MAX_LENGTH + " characters or fewer:" + keyword);
        if (_keywords.contains(keyword))
            throw new IllegalArgumentException("Keyword has already been added: " + keyword);
        else
            _keywords.add(keyword);

        return keyword;
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
