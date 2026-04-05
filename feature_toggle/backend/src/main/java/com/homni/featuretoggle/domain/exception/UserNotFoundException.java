package com.homni.featuretoggle.domain.exception;

import com.homni.featuretoggle.domain.model.UserId;

/**
 * Thrown when a user is not found by their identity.
 */
public final class UserNotFoundException extends DomainNotFoundException {

    /**
     * @param id the identity of the user that was not found
     */
    public UserNotFoundException(UserId id) {
        super("User not found: " + id.value);
    }

    /**
     * @param oidcSubject the OIDC subject of the user that was not found
     */
    public UserNotFoundException(String oidcSubject) {
        super("User not found by OIDC subject: " + oidcSubject);
    }
}
