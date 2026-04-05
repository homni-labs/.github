package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when attempting to create an environment with a name that already exists.
 */
public final class EnvironmentAlreadyExistsException extends DomainConflictException {

    /**
     * @param name the environment name that already exists
     */
    public EnvironmentAlreadyExistsException(String name) {
        super("Environment '%s' already exists".formatted(name));
    }
}
