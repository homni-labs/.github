package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when an API key name violates naming constraints.
 */
public final class InvalidApiKeyNameException extends DomainValidationException {

    /**
     * Creates an exception for an invalid API key name.
     *
     * @param name the invalid name value
     */
    public InvalidApiKeyNameException(String name) {
        super("API key name must be 1-255 non-blank characters, got: '%s'".formatted(name));
    }
}
