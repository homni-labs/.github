package com.homni.featuretoggle.application.port.out;

import com.homni.featuretoggle.domain.model.FeatureToggle;
import com.homni.featuretoggle.domain.model.FeatureToggleId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for persisting feature toggles.
 */
public interface FeatureToggleRepositoryPort {

    /**
     * Saves a feature toggle (insert or update).
     *
     * @param toggle the toggle to save
     */
    void save(FeatureToggle toggle);

    /**
     * Finds a feature toggle by its identity.
     *
     * @param id the toggle identity
     * @return the toggle if found
     */
    Optional<FeatureToggle> findById(FeatureToggleId id);

    /**
     * Lists feature toggles with optional filtering.
     *
     * @param enabled     filter by enabled status, or {@code null} for all
     * @param environment filter by environment name, or {@code null} for all
     * @param offset      the number of rows to skip
     * @param limit       the maximum number of rows to return
     * @return the matching toggles
     */
    List<FeatureToggle> findAll(Boolean enabled, String environment, int offset, int limit);

    /**
     * Counts feature toggles matching the filters.
     *
     * @param enabled     filter by enabled status, or {@code null} for all
     * @param environment filter by environment name, or {@code null} for all
     * @return the count of matching toggles
     */
    long count(Boolean enabled, String environment);

    /**
     * Deletes a feature toggle by its identity.
     *
     * @param id the toggle identity
     */
    void deleteById(FeatureToggleId id);
}
