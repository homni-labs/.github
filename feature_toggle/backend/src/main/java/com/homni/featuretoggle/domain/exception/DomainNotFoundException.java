package com.homni.featuretoggle.domain.exception;

/**
 * Thrown when a requested domain entity is not found.
 */
public abstract class DomainNotFoundException extends DomainException {

    /**
     * Creates a not-found exception with the given message.
     *
     * @param message description of the missing entity
     */
    protected DomainNotFoundException(String message) {
        super(message);
    }
}
