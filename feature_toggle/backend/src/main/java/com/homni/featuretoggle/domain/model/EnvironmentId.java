package com.homni.featuretoggle.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identity of a deployment environment.
 */
public final class EnvironmentId {

    public final UUID value;

    /**
     * Creates an environment identity from an existing UUID.
     *
     * @param value the UUID value
     */
    public EnvironmentId(UUID value) {
        this.value = Objects.requireNonNull(value, "EnvironmentId must not be null");
    }

    /**
     * Generates a new random environment identity.
     */
    public EnvironmentId() {
        this(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof EnvironmentId that && value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
