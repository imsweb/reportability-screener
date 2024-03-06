/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb;

import org.ahocorasick.trie.Emit;

import com.imsweb.ReportabilityScreener.Group;

public class Keyword {

    private final String _keyword;
    private final int _start;
    private final int _end;
    private final Group _group;
    private boolean _ignored;

    public Keyword(String keyword, int start, int end, Group group) {
        _keyword = keyword;
        _start = start;
        _end = end;
        _group = group;
    }

    public Keyword(Emit emit, Group group) {
        _keyword = emit.getKeyword();
        _start = emit.getStart();
        _end = emit.getEnd();
        _group = group;
    }

    public String getKeyword() {
        return _keyword;
    }

    public int getStart() {
        return _start;
    }

    public int getEnd() {
        return _end;
    }

    public Group getGroup() {
        return _group;
    }

    public boolean isIgnored() {
        return _ignored;
    }

    public void setIgnored(boolean ignored) {
        _ignored = ignored;
    }

}
