package com.orbitalix.exception;

public final class SensorFailureException extends OrbitalIXException {
    public SensorFailureException(String message) { super(message); }
    public SensorFailureException(String message, Throwable cause) { super(message, cause); }
}
