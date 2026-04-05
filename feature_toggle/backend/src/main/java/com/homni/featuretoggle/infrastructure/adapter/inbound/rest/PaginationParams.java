package com.homni.featuretoggle.infrastructure.adapter.inbound.rest;

/**
 * Normalizes raw pagination parameters from API requests to safe defaults.
 *
 * @param page the zero-based page number
 * @param size the number of items per page
 */
record PaginationParams(int page, int size) {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    /**
     * Normalizes nullable raw parameters to bounded values.
     *
     * @param rawPage the raw page number, may be {@code null}
     * @param rawSize the raw page size, may be {@code null}
     * @return normalized pagination parameters
     */
    static PaginationParams of(Integer rawPage, Integer rawSize) {
        int page = (rawPage != null && rawPage >= 0) ? rawPage : 0;
        int size = (rawSize != null && rawSize >= 1) ? Math.min(rawSize, MAX_SIZE) : DEFAULT_SIZE;
        return new PaginationParams(page, size);
    }
}
