package com.homni.featuretoggle.domain.model;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Result of issuing a new API key. Contains both the persisted key and the raw token
 * that must be shown to the user exactly once.
 */
public final class IssuedApiKey {

    private static final String TOKEN_PREFIX = "hft_";
    private static final int TOKEN_BYTE_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public final ApiKey apiKey;
    public final String rawToken;

    /**
     * Issues a new API key with a securely generated token.
     *
     * @param name      the key name (1-255 non-blank characters)
     * @param expiresAt the expiration timestamp, may be {@code null} for no expiration
     * @throws com.homni.featuretoggle.domain.exception.InvalidApiKeyNameException if the name is invalid
     */
    public IssuedApiKey(String name, Instant expiresAt) {
        this.rawToken = generateToken();
        TokenHash tokenHash = TokenHash.from(this.rawToken);
        this.apiKey = new ApiKey(name, tokenHash, expiresAt);
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(bytes);
        return TOKEN_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
