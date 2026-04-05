package com.homni.featuretoggle.domain.exception;

import com.homni.featuretoggle.domain.model.FeatureToggleId;

/**
 * Thrown when attempting to disable a toggle that is already disabled.
 */
public final class ToggleAlreadyDisabledException extends DomainConflictException {

    /**
     * @param id   the toggle identity
     * @param name the toggle name
     */
    public ToggleAlreadyDisabledException(FeatureToggleId id, String name) {
        super("Toggle [id=%s, name=%s] is already disabled".formatted(id.value, name));
    }
}
