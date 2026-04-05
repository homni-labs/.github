package com.homni.featuretoggle.domain.model;

import com.homni.featuretoggle.domain.exception.EmptyEnvironmentsException;
import com.homni.featuretoggle.domain.exception.InvalidToggleNameException;
import com.homni.featuretoggle.domain.exception.ToggleAlreadyDisabledException;
import com.homni.featuretoggle.domain.exception.ToggleAlreadyEnabledException;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class FeatureToggle {

    public final FeatureToggleId id;
    public final Instant createdAt;

    private String name;
    private String description;
    private boolean enabled;
    private Instant updatedAt;
    private final Set<String> environments;

    /**
     * Creates a new feature toggle in a disabled state.
     *
     * @param name         the toggle name (1-255 non-blank characters)
     * @param description  the human-readable description, may be {@code null}
     * @param environments the initial set of environment names, must not be empty
     * @throws InvalidToggleNameException if the name is invalid
     * @throws EmptyEnvironmentsException if environments is empty
     */
    public FeatureToggle(String name, String description, Set<String> environments) {
        this.id = new FeatureToggleId();
        this.name = validateName(name);
        this.description = description;
        this.enabled = false;
        this.environments = validateEnvironments(name, environments);
        this.createdAt = Instant.now();
        this.updatedAt = null;
    }

    /**
     * Restores a feature toggle from persistent storage.
     *
     * @param id           the toggle identity
     * @param name         the toggle name
     * @param description  the toggle description, may be {@code null}
     * @param enabled      whether the toggle is enabled
     * @param environments the assigned environment names
     * @param createdAt    the creation timestamp
     * @param updatedAt    the last modification timestamp, may be {@code null}
     */
    public FeatureToggle(FeatureToggleId id, String name, String description,
                         boolean enabled, Set<String> environments,
                         Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.name = validateName(name);
        this.description = description;
        this.enabled = enabled;
        this.environments = new LinkedHashSet<>(environments != null ? environments : Set.of());
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = updatedAt;
    }

    /**
     * Enables this feature toggle and records the modification time.
     *
     * @return this toggle for chaining
     * @throws ToggleAlreadyEnabledException if the toggle is already enabled
     */
    public FeatureToggle enable() {
        if (this.enabled) {
            throw new ToggleAlreadyEnabledException(this.id, this.name);
        }
        this.enabled = true;
        this.updatedAt = Instant.now();
        return this;
    }

    /**
     * Disables this feature toggle and records the modification time.
     *
     * @return this toggle for chaining
     * @throws ToggleAlreadyDisabledException if the toggle is already disabled
     */
    public FeatureToggle disable() {
        if (!this.enabled) {
            throw new ToggleAlreadyDisabledException(this.id, this.name);
        }
        this.enabled = false;
        this.updatedAt = Instant.now();
        return this;
    }

    /**
     * Updates the mutable fields of this toggle.
     * Each parameter may be {@code null} to skip updating that field.
     *
     * @param newName         the new name, or {@code null} to keep current
     * @param newDescription  the new description, or {@code null} to keep current
     * @param newEnvironments the new set of environment names, or {@code null} to keep current
     */
    public void update(String newName, String newDescription, Set<String> newEnvironments) {
        if (newName != null) {
            this.name = validateName(newName);
        }
        if (newDescription != null) {
            this.description = newDescription;
        }
        if (newEnvironments != null) {
            validateEnvironments(this.name, newEnvironments);
            this.environments.clear();
            this.environments.addAll(newEnvironments);
        }
        this.updatedAt = Instant.now();
    }

    /**
     * Returns the current name of this toggle.
     *
     * @return the toggle name
     */
    public String name() {
        return this.name;
    }

    /**
     * Returns the toggle description text.
     *
     * @return the description, may be {@code null}
     */
    public String description() {
        return this.description;
    }

    /**
     * Indicates whether this toggle is currently enabled.
     *
     * @return {@code true} if the toggle is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Returns the assigned environment names as an immutable copy.
     *
     * @return the set of environment names
     */
    public Set<String> environments() {
        return Set.copyOf(this.environments);
    }

    /**
     * Returns the instant when this toggle was last modified.
     *
     * @return the last modification timestamp, may be {@code null}
     */
    public Instant lastModifiedAt() {
        return this.updatedAt;
    }

    private LinkedHashSet<String> validateEnvironments(String toggleName,
                                                       Set<String> environments) {
        if (environments == null || environments.isEmpty()) {
            throw new EmptyEnvironmentsException(toggleName);
        }
        return new LinkedHashSet<>(environments);
    }

    private String validateName(String name) {
        if (name == null || name.isBlank() || name.length() > 255) {
            throw new InvalidToggleNameException(String.valueOf(name));
        }
        return name;
    }
}
