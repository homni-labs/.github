package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when an environment name fails validation.
 */
public final class InvalidEnvironmentNameException extends DomainValidationException {

    /**
     * @param name   the invalid name
     * @param reason the reason the name is invalid
     */
    public InvalidEnvironmentNameException(String name, String reason) {
        super("Environment name '%s': %s".formatted(name, reason));
    }
}
