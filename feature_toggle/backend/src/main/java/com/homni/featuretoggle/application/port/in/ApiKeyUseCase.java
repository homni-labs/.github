package com.homni.featuretoggle.application.port.in;

import com.homni.featuretoggle.domain.model.ApiKeyId;
import com.homni.featuretoggle.domain.model.ApiKeyPage;
import com.homni.featuretoggle.domain.model.IssuedApiKey;

import java.time.Instant;

/**
 * Input port for API key management operations.
 */
public interface ApiKeyUseCase {

    /**
     * Issues a new API key.
     *
     * @param name      the key name
     * @param expiresAt the expiration timestamp, may be {@code null} for no expiration
     * @return the issued key containing both the API key and the raw token
     * @throws com.homni.featuretoggle.domain.exception.InvalidApiKeyNameException if name is invalid
     */
    IssuedApiKey issue(String name, Instant expiresAt);

    /**
     * Lists API keys with pagination.
     *
     * @param page zero-based page number
     * @param size number of items per page
     * @return a page of API keys ordered by creation date descending
     */
    ApiKeyPage list(int page, int size);

    /**
     * Revokes an API key.
     *
     * @param id the API key identity
     * @throws com.homni.featuretoggle.domain.exception.ApiKeyNotFoundException if not found
     */
    void revoke(ApiKeyId id);
}
