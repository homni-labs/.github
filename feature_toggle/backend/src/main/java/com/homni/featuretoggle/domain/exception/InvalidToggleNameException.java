package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when a toggle name violates naming constraints.
 */
public final class InvalidToggleNameException extends DomainValidationException {

    /**
     * Creates an exception for an invalid toggle name.
     *
     * @param name the invalid name value
     */
    public InvalidToggleNameException(String name) {
        super("Toggle name must be 1-255 non-blank characters, got: '%s'".formatted(name));
    }
}
