package com.orbitalix.exception;

public final class CommunicationLossException extends OrbitalIXException {
    public CommunicationLossException(String message) { super(message); }
    public CommunicationLossException(String message, Throwable cause) { super(message, cause); }
}
