package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when a domain invariant is violated during object creation or mutation.
 */
public abstract class DomainValidationException extends DomainException {

    /**
     * Creates a validation exception with the given message.
     *
     * @param message description of the validation failure
     */
    protected DomainValidationException(String message) {
        super(message);
    }
}
