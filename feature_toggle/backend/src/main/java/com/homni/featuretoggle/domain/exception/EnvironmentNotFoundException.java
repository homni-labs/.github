package com.homni.featuretoggle.domain.exception;

import com.homni.featuretoggle.domain.model.EnvironmentId;

/**
 * Thrown when an environment is not found by its identity.
 */
public final class EnvironmentNotFoundException extends DomainNotFoundException {

    /**
     * @param id the identity of the environment that was not found
     */
    public EnvironmentNotFoundException(EnvironmentId id) {
        super("Environment not found: " + id.value);
    }
}
