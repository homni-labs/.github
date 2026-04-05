package com.homni.featuretoggle.application.port.in;

import com.homni.featuretoggle.domain.model.AppUser;
import com.homni.featuretoggle.domain.model.Role;
import com.homni.featuretoggle.domain.model.UserId;
import com.homni.featuretoggle.domain.model.UserPage;

/**
 * Input port for user management operations.
 */
public interface UserUseCase {

    /**
     * Finds or creates a user by OIDC subject, binding the subject if the email matches.
     *
     * @param oidcSubject the OIDC subject identifier
     * @param email       the user's email address
     * @param name        the user's display name, may be {@code null}
     * @return the found or created user
     */
    AppUser findOrCreateByOidcSubject(String oidcSubject, String email, String name);

    /**
     * Finds a user by their OIDC subject identifier.
     *
     * @param oidcSubject the OIDC subject
     * @return the found user
     * @throws com.homni.featuretoggle.domain.exception.UserNotFoundException if not found
     */
    AppUser findByOidcSubject(String oidcSubject);

    /**
     * Finds a user by their identity.
     *
     * @param id the user identity
     * @return the found user
     * @throws com.homni.featuretoggle.domain.exception.UserNotFoundException if not found
     */
    AppUser findById(UserId id);

    /**
     * Lists users with pagination.
     *
     * @param page zero-based page number
     * @param size number of items per page
     * @return a page of users ordered by email
     */
    UserPage list(int page, int size);

    /**
     * Changes the role of a user.
     *
     * @param id   the user identity
     * @param role the new role
     * @return the updated user
     * @throws com.homni.featuretoggle.domain.exception.UserNotFoundException if not found
     */
    AppUser changeRole(UserId id, Role role);

    /**
     * Disables a user account.
     *
     * @param id the user identity
     * @return the updated user
     * @throws com.homni.featuretoggle.domain.exception.UserNotFoundException if not found
     */
    AppUser disable(UserId id);

    /**
     * Activates a user account.
     *
     * @param id the user identity
     * @return the updated user
     * @throws com.homni.featuretoggle.domain.exception.UserNotFoundException if not found
     */
    AppUser activate(UserId id);

    /**
     * Updates user role and/or active status in a single operation.
     *
     * @param id     the user identity
     * @param role   the new role, may be {@code null} to skip
     * @param active the new active status, may be {@code null} to skip
     * @return the updated user
     * @throws com.homni.featuretoggle.domain.exception.UserNotFoundException if not found
     */
    AppUser updateUser(UserId id, Role role, Boolean active);

    /**
     * Deletes a user.
     *
     * @param id the user identity
     * @throws com.homni.featuretoggle.domain.exception.UserNotFoundException if not found
     */
    void delete(UserId id);
}
