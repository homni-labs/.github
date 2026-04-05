package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when attempting to create a toggle with a name that already exists.
 */
public final class ToggleAlreadyExistsException extends DomainConflictException {

    /**
     * @param name the toggle name that already exists
     */
    public ToggleAlreadyExistsException(String name) {
        super("Toggle '%s' already exists".formatted(name));
    }
}
