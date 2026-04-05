package com.homni.featuretoggle.domain.exception;

import com.homni.featuretoggle.domain.model.ApiKeyId;

/**
 * Thrown when attempting to revoke an API key that is already revoked.
 */
public final class ApiKeyAlreadyRevokedException extends DomainConflictException {

    /**
     * @param id   the API key identity
     * @param name the API key name
     */
    public ApiKeyAlreadyRevokedException(ApiKeyId id, String name) {
        super("API key [id=%s, name=%s] is already revoked".formatted(id.value, name));
    }
}
