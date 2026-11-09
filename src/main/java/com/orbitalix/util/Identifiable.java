package com.orbitalix.util;

/** Provides stable identifiers for diagnostics and bounded generic helpers. */
public interface Identifiable {
    String id();

    static <T extends Identifiable> T requireId(T value, String expectedId) {
        if (value == null || !value.id().equals(expectedId)) {
            throw new IllegalArgumentException("Expected item with id " + expectedId);
        }
        return value;
    }
}
