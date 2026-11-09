package com.orbitalix.report;

public interface ReportVisitor<T> {
    T visit(MissionReport report);
}
