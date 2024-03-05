/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb;

import java.util.List;

import com.imsweb.ReportabilityScreener.ReportabilityResult;

public class ScreeningResult {

    private ReportabilityResult result;
    private List<Keyword> positiveKeywords;
    private List<Keyword> negativeKeywords;
    private List<Keyword> otherKeywords;

    public ReportabilityResult getResult() {
        return result;
    }

    public void setResult(ReportabilityResult result) {
        this.result = result;
    }

    public List<Keyword> getPositiveKeywords() {
        return positiveKeywords;
    }

    public void setPositiveKeywords(List<Keyword> positiveKeywords) {
        this.positiveKeywords = positiveKeywords;
    }

    public List<Keyword> getNegativeKeywords() {
        return negativeKeywords;
    }

    public void setNegativeKeywords(List<Keyword> negativeKeywords) {
        this.negativeKeywords = negativeKeywords;
    }

    public List<Keyword> getOtherKeywords() {
        return otherKeywords;
    }

    public void setOtherKeywords(List<Keyword> otherKeywords) {
        this.otherKeywords = otherKeywords;
    }
}
