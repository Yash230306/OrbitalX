package com.orbitalix.exception;

public final class UnrecoverableFaultException extends OrbitalIXException {
    public UnrecoverableFaultException(String message) { super(message); }
    public UnrecoverableFaultException(String message, Throwable cause) { super(message, cause); }
}
