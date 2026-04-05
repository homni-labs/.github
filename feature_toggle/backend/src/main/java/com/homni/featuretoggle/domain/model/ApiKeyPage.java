package com.homni.featuretoggle.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * A page of API keys with pagination metadata.
 *
 * @param items         the API keys on this page
 * @param totalElements total count across all pages
 */
public record ApiKeyPage(List<ApiKey> items, long totalElements) {

    /**
     * Creates a page with a defensive copy of the items list.
     */
    public ApiKeyPage {
        Objects.requireNonNull(items, "items must not be null");
        items = List.copyOf(items);
    }
}
