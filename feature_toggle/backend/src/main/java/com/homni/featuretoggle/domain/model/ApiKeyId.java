package com.homni.featuretoggle.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identity of an API key.
 */
public final class ApiKeyId {

    public final UUID value;

    /**
     * Creates an API key identity from an existing UUID.
     *
     * @param value the UUID value
     */
    public ApiKeyId(UUID value) {
        this.value = Objects.requireNonNull(value, "ApiKeyId must not be null");
    }

    /**
     * Generates a new random API key identity.
     */
    public ApiKeyId() {
        this(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ApiKeyId that && value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
