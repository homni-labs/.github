package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when a feature toggle is created or updated with an empty environments set.
 */
public final class EmptyEnvironmentsException extends DomainValidationException {

    /**
     * @param toggleName the toggle name
     */
    public EmptyEnvironmentsException(String toggleName) {
        super("Toggle '%s': environments must not be empty".formatted(toggleName));
    }
}
