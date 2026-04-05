package com.homni.featuretoggle.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identity of an application user.
 */
public final class UserId {

    public final UUID value;

    /**
     * Creates a user identity from an existing UUID.
     *
     * @param value the UUID value
     */
    public UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "UserId must not be null");
    }

    /**
     * Generates a new random user identity.
     */
    public UserId() {
        this(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof UserId that && value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
