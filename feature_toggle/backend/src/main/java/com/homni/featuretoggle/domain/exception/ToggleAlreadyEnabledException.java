package com.homni.featuretoggle.domain.exception;

import com.homni.featuretoggle.domain.model.FeatureToggleId;

/**
 * Thrown when attempting to enable a toggle that is already enabled.
 */
public final class ToggleAlreadyEnabledException extends DomainConflictException {

    /**
     * @param id   the toggle identity
     * @param name the toggle name
     */
    public ToggleAlreadyEnabledException(FeatureToggleId id, String name) {
        super("Toggle [id=%s, name=%s] is already enabled".formatted(id.value, name));
    }
}
