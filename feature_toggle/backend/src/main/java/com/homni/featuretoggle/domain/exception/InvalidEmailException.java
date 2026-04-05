package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when an email address fails format validation.
 */
public final class InvalidEmailException extends DomainValidationException {

    /**
     * Creates an exception for an invalid email address.
     *
     * @param value the invalid email value
     */
    public InvalidEmailException(String value) {
        super("Invalid email format: '%s'".formatted(value));
    }
}
