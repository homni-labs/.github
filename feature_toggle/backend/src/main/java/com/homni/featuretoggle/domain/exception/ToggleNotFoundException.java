package com.homni.featuretoggle.domain.exception;

import com.homni.featuretoggle.domain.model.FeatureToggleId;

/**
 * Thrown when a feature toggle is not found by its identity.
 */
public final class ToggleNotFoundException extends DomainNotFoundException {

    /**
     * @param id the identity of the toggle that was not found
     */
    public ToggleNotFoundException(FeatureToggleId id) {
        super("Feature toggle not found: " + id.value);
    }
}
