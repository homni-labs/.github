package com.homni.featuretoggle.domain.model;

/**
 * Defines the authorization roles available to application users.
 */
public enum Role {

    /** Full administrative access. */
    ADMIN,

    /** Can create, enable, and disable toggles. */
    EDITOR,

    /** Read-only access to toggles. */
    READER
}
