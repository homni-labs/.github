package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when attempting to delete an environment that is still referenced by feature toggles.
 */
public final class EnvironmentInUseException extends DomainConflictException {

    /**
     * @param name the environment name that is still in use
     */
    public EnvironmentInUseException(String name) {
        super("Environment '%s' is still used by one or more feature toggles".formatted(name));
    }
}
