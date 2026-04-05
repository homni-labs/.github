package com.homni.featuretoggle.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * A page of feature toggles with pagination metadata.
 *
 * @param items         the toggles on this page
 * @param totalElements total count across all pages
 */
public record TogglePage(List<FeatureToggle> items, long totalElements) {

    /**
     * Creates a page with a defensive copy of the items list.
     */
    public TogglePage {
        Objects.requireNonNull(items, "items must not be null");
        items = List.copyOf(items);
    }
}
