package com.homni.featuretoggle.domain.model;

import com.homni.featuretoggle.domain.exception.InvalidEnvironmentNameException;

import java.time.Instant;
import java.util.Objects;

/**
 * Deployment environment that feature toggles can be assigned to.
 *
 * <p>Environments are user-managed entities with unique names.
 * Name is normalized to uppercase on creation.
 */
public final class Environment {

    public final EnvironmentId id;
    public final Instant createdAt;

    private String name;

    /**
     * Creates a new environment.
     *
     * @param name the environment name (1-50 non-blank characters)
     * @throws InvalidEnvironmentNameException if the name is invalid
     */
    public Environment(String name) {
        this.id = new EnvironmentId();
        this.name = validateAndNormalize(name);
        this.createdAt = Instant.now();
    }

    /**
     * Restores an environment from persistent storage.
     *
     * @param id        the environment identity
     * @param name      the environment name
     * @param createdAt the creation timestamp
     */
    public Environment(EnvironmentId id, String name, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.name = validateAndNormalize(name);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    /**
     * Returns the environment name.
     *
     * @return the uppercase environment name
     */
    public String name() {
        return this.name;
    }

    private String validateAndNormalize(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidEnvironmentNameException(name, "must not be blank");
        }
        String normalized = name.trim().toUpperCase();
        if (normalized.length() > 50) {
            throw new InvalidEnvironmentNameException(name, "must not exceed 50 characters");
        }
        if (!normalized.matches("^[A-Z][A-Z0-9_]*$")) {
            throw new InvalidEnvironmentNameException(name,
                    "must start with a letter and contain only letters, digits, and underscores");
        }
        return normalized;
    }
}
