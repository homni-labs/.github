package com.homni.featuretoggle.domain.model;

import java.time.Instant;
import java.util.Objects;

public final class AppUser {

    public final UserId id;
    public final Email email;
    public final Instant createdAt;

    private String oidcSubject;
    private String name;
    private Role role;
    private boolean active;
    private Instant updatedAt;

    /**
     * Creates a new active user with an OIDC subject already bound.
     *
     * @param oidcSubject the OIDC subject identifier
     * @param email       the user's email address (will be validated)
     * @param name        the display name, may be {@code null}
     * @param role        the initial role
     * @throws com.homni.featuretoggle.domain.exception.InvalidEmailException if email format is invalid
     */
    public AppUser(String oidcSubject, String email, String name, Role role) {
        Objects.requireNonNull(oidcSubject, "oidcSubject must not be null");
        this.id = new UserId();
        this.oidcSubject = oidcSubject;
        this.email = new Email(email);
        this.name = name;
        this.role = Objects.requireNonNull(role);
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = null;
    }

    /**
     * Creates a new active user without an OIDC subject (pre-provisioned).
     *
     * @param email the user's email address (will be validated)
     * @param name  the display name, may be {@code null}
     * @param role  the initial role
     * @throws com.homni.featuretoggle.domain.exception.InvalidEmailException if email format is invalid
     */
    public AppUser(String email, String name, Role role) {
        this.id = new UserId();
        this.oidcSubject = null;
        this.email = new Email(email);
        this.name = name;
        this.role = Objects.requireNonNull(role);
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = null;
    }

    /**
     * Restores an existing user from persistent storage.
     *
     * @param id          the user identity
     * @param oidcSubject the OIDC subject, may be {@code null}
     * @param email       the validated email
     * @param name        the display name, may be {@code null}
     * @param role        the user's role
     * @param active      whether the user is active
     * @param createdAt   the creation timestamp
     * @param updatedAt   the last modification timestamp, may be {@code null}
     */
    public AppUser(UserId id, String oidcSubject, Email email, String name,
                   Role role, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.oidcSubject = oidcSubject;
        this.email = Objects.requireNonNull(email);
        this.name = name;
        this.role = Objects.requireNonNull(role);
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = updatedAt;
    }

    /**
     * Binds an OIDC subject identifier to this user.
     *
     * @param oidcSubject the OIDC subject to bind
     */
    public void bindOidcSubject(String oidcSubject) {
        this.oidcSubject = Objects.requireNonNull(oidcSubject);
        this.updatedAt = Instant.now();
    }

    /**
     * Changes the role of this user.
     *
     * @param newRole the new role to assign
     */
    public void changeRole(Role newRole) {
        this.role = Objects.requireNonNull(newRole);
        this.updatedAt = Instant.now();
    }

    /**
     * Disables this user, preventing authentication.
     */
    public void disable() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    /**
     * Activates this user, allowing authentication.
     */
    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    /**
     * Returns the OIDC subject identifier, if bound.
     *
     * @return the OIDC subject, may be {@code null}
     */
    public String oidcSubject() {
        return this.oidcSubject;
    }

    /**
     * Returns the display name of this user.
     *
     * @return the user's name, may be {@code null}
     */
    public String displayName() {
        return this.name;
    }

    /**
     * Returns the current role assigned to this user.
     *
     * @return the user's role
     */
    public Role currentRole() {
        return this.role;
    }

    /**
     * Indicates whether this user account is active.
     *
     * @return {@code true} if the user is active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Returns the instant when this user was last modified.
     *
     * @return the last modification timestamp, may be {@code null}
     */
    public Instant lastModifiedAt() {
        return this.updatedAt;
    }

    /**
     * Checks whether this user can have an OIDC subject bound.
     *
     * @return {@code true} if no OIDC subject is currently bound
     */
    public boolean canBindOidc() {
        return this.oidcSubject == null;
    }

    /**
     * Checks whether this user is allowed to authenticate.
     *
     * @return {@code true} if the user is active
     */
    public boolean canAuthenticate() {
        return this.active;
    }

}
