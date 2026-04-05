package com.homni.featuretoggle.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * A page of users with pagination metadata.
 *
 * @param items         the users on this page
 * @param totalElements total count across all pages
 */
public record UserPage(List<AppUser> items, long totalElements) {

    /**
     * Creates a page with a defensive copy of the items list.
     */
    public UserPage {
        Objects.requireNonNull(items, "items must not be null");
        items = List.copyOf(items);
    }
}
