package com.homni.featuretoggle.domain.exception;

/**
 * Base class for all domain exceptions.
 */
public abstract class DomainException extends RuntimeException {

    /**
     * Creates a domain exception with the given message.
     *
     * @param message description of the error
     */
    protected DomainException(String message) {
        super(message);
    }
}
