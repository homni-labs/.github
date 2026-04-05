package com.homni.featuretoggle.domain.exception;

import com.homni.featuretoggle.domain.model.ApiKeyId;

/**
 * Thrown when an API key is not found by its identity.
 */
public final class ApiKeyNotFoundException extends DomainNotFoundException {

    /**
     * @param id the identity of the API key that was not found
     */
    public ApiKeyNotFoundException(ApiKeyId id) {
        super("API key not found: " + id.value);
    }
}
