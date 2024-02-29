/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb;

import org.ahocorasick.trie.Emit;

import com.imsweb.ReportabilityScreener.Group;

public class Keyword {

    private String _keyword;
    private int _start;
    private int _end;
    private Group _group;
    private boolean _ignored;

    public Keyword(Emit emit, Group group) {
        _keyword = emit.getKeyword();
        _start = emit.getStart();
        _end = emit.getEnd();
        _group = group;
    }

    public String getKeyword() {
        return _keyword;
    }

    public void setKeyword(String keyword) {
        _keyword = keyword;
    }

    public int getStart() {
        return _start;
    }

    public void setStart(int start) {
        _start = start;
    }

    public int getEnd() {
        return _end;
    }

    public void setEnd(int end) {
        _end = end;
    }

    public Group getGroup() {
        return _group;
    }

    public void setGroup(Group group) {
        _group = group;
    }

    public boolean isIgnored() {
        return _ignored;
    }

    public void setIgnored(boolean ignored) {
        _ignored = ignored;
    }
}
