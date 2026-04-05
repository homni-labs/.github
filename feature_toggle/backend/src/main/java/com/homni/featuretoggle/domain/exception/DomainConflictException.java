package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when an operation conflicts with existing domain state.
 */
public abstract class DomainConflictException extends DomainException {

    /**
     * Creates a conflict exception with the given message.
     *
     * @param message description of the conflict
     */
    protected DomainConflictException(String message) {
        super(message);
    }
}
