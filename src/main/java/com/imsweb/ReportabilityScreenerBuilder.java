/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb;

import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.exceptions.CsvException;

import com.imsweb.ReportabilityScreener.Group;

public class ReportabilityScreenerBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ReportabilityScreener.class);
    private final TrieBuilder _positiveTrieBuilder;
    private final TrieBuilder _negativeTrieBuilder;
    private final TrieBuilder _otherTrieBuilder;

    public ReportabilityScreenerBuilder() {
        _positiveTrieBuilder = Trie.builder();
        _negativeTrieBuilder = Trie.builder();
        _otherTrieBuilder = Trie.builder();
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
        try (CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/default.keyword.list")).withCSVParser(new RFC4180Parser()).withSkipLines(1).build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                add(line[0], getGroupFromString(line[1]));
            }
        }
        catch (CsvException | IOException e) {
            LOG.error("Unable to parse default keyword list.", e);
        }
        catch (InvalidParameterException e) {
            LOG.error(e.getMessage(), e);
        }

    }

    private Group getGroupFromString(String groupString) {
        Group group;
        switch (groupString) {
            case "Positive":
                group = Group.POSITIVE;
                break;
            case "Negative":
                group = Group.NEGATIVE;
                break;
            case "Other":
                group = Group.OTHER;
                break;
            default:
                throw new InvalidParameterException("Unexpected group value: " + groupString);
        }
        return group;
    }


}
