package com.homni.featuretoggle.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identity of a feature toggle.
 */
public final class FeatureToggleId {

    public final UUID value;

    /**
     * Creates a toggle identity from an existing UUID.
     *
     * @param value the UUID value
     */
    public FeatureToggleId(UUID value) {
        this.value = Objects.requireNonNull(value, "FeatureToggleId must not be null");
    }

    /**
     * Generates a new random toggle identity.
     */
    public FeatureToggleId() {
        this(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof FeatureToggleId that && value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
