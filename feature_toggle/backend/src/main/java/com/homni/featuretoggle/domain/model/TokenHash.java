package com.homni.featuretoggle.domain.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * SHA-256 hash of an API key token.
 */
public final class TokenHash {

    public final String value;

    /**
     * Wraps a hex-encoded SHA-256 hash value (e.g. from database).
     *
     * @param value the hex-encoded hash
     */
    public TokenHash(String value) {
        this.value = Objects.requireNonNull(value, "TokenHash must not be null");
    }

    /**
     * Computes a SHA-256 hash from a raw token string.
     *
     * @param rawToken the raw token to hash
     * @return the computed token hash
     */
    public static TokenHash from(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return new TokenHash(HexFormat.of().formatHex(hashBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof TokenHash that && value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
