package com.orbitalix.report;

public interface Visitable {
    <T> T accept(ReportVisitor<T> visitor);
}
