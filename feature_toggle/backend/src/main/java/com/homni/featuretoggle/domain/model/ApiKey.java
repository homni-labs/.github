package com.homni.featuretoggle.domain.model;

import com.homni.featuretoggle.domain.exception.ApiKeyAlreadyRevokedException;
import com.homni.featuretoggle.domain.exception.InvalidApiKeyNameException;

import java.time.Instant;
import java.util.Objects;

public final class ApiKey {

    private static final String TOKEN_PREFIX = "hft_";

    public final ApiKeyId id;
    public final String name;
    public final Instant createdAt;
    public final TokenHash tokenHash;
    public final Instant expiresAt;

    private boolean active;

    /**
     * Creates a new API key with a pre-computed token hash.
     *
     * @param name      the key name (1-255 non-blank characters)
     * @param tokenHash the SHA-256 hash of the token
     * @param expiresAt the expiration timestamp, may be {@code null} for no expiration
     * @throws InvalidApiKeyNameException if the name is invalid
     */
    public ApiKey(String name, TokenHash tokenHash, Instant expiresAt) {
        this.id = new ApiKeyId();
        this.name = validateName(name);
        this.tokenHash = Objects.requireNonNull(tokenHash);
        this.active = true;
        this.createdAt = Instant.now();
        this.expiresAt = expiresAt;
    }

    /**
     * Restores an existing API key from persistent storage.
     *
     * @param id        the API key identity
     * @param name      the key name
     * @param tokenHash the SHA-256 hash of the token
     * @param active    whether the key is active
     * @param createdAt the creation timestamp
     * @param expiresAt the expiration timestamp, may be {@code null}
     */
    public ApiKey(ApiKeyId id, String name, TokenHash tokenHash, boolean active,
                  Instant createdAt, Instant expiresAt) {
        this.id = Objects.requireNonNull(id);
        this.name = validateName(name);
        this.tokenHash = Objects.requireNonNull(tokenHash);
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.expiresAt = expiresAt;
    }

    /**
     * Revokes this API key, making it inactive.
     *
     * @throws ApiKeyAlreadyRevokedException if the key is already revoked
     */
    public void revoke() {
        if (!this.active) {
            throw new ApiKeyAlreadyRevokedException(this.id, this.name);
        }
        this.active = false;
    }

    /**
     * Checks whether this key is valid (active and not expired).
     *
     * @return {@code true} if the key is active and has not expired
     */
    public boolean isValid() {
        return active && (expiresAt == null || Instant.now().isBefore(expiresAt));
    }

    /**
     * Indicates whether this API key is currently active (not revoked).
     *
     * @return {@code true} if the key is active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Returns a masked representation of the token for safe display.
     *
     * @return the masked token string
     */
    public String maskedToken() {
        String hash = tokenHash.value;
        String suffix = hash.substring(Math.max(0, hash.length() - 4));
        return TOKEN_PREFIX + "****" + suffix;
    }

    private String validateName(String name) {
        if (name == null || name.isBlank() || name.length() > 255) {
            throw new InvalidApiKeyNameException(String.valueOf(name));
        }
        return name;
    }
}
