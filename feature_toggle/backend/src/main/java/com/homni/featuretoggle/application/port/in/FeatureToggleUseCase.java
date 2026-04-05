package com.homni.featuretoggle.application.port.in;

import com.homni.featuretoggle.domain.model.FeatureToggle;
import com.homni.featuretoggle.domain.model.FeatureToggleId;
import com.homni.featuretoggle.domain.model.TogglePage;

import java.util.Set;

/**
 * Input port for feature toggle operations.
 */
public interface FeatureToggleUseCase {

    /**
     * Creates a new feature toggle.
     *
     * @param name         the toggle name
     * @param description  the toggle description, may be {@code null}
     * @param environments the initial set of environment names
     * @return the created toggle
     * @throws com.homni.featuretoggle.domain.exception.InvalidToggleNameException if name is invalid
     */
    FeatureToggle create(String name, String description, Set<String> environments);

    /**
     * Finds a feature toggle by its identity.
     *
     * @param id the toggle identity
     * @return the found toggle
     * @throws com.homni.featuretoggle.domain.exception.ToggleNotFoundException if not found
     */
    FeatureToggle findById(FeatureToggleId id);

    /**
     * Lists feature toggles with optional filtering and pagination.
     *
     * @param enabled     filter by enabled status, or {@code null} for all
     * @param environment filter by environment name, or {@code null} for all
     * @param page        the zero-based page number
     * @param size        the page size
     * @return a page of toggles
     */
    TogglePage list(Boolean enabled, String environment, int page, int size);

    /**
     * Enables a feature toggle.
     *
     * @param id the toggle identity
     * @return the updated toggle
     * @throws com.homni.featuretoggle.domain.exception.ToggleNotFoundException if not found
     */
    FeatureToggle enable(FeatureToggleId id);

    /**
     * Disables a feature toggle.
     *
     * @param id the toggle identity
     * @return the updated toggle
     * @throws com.homni.featuretoggle.domain.exception.ToggleNotFoundException if not found
     */
    FeatureToggle disable(FeatureToggleId id);

    /**
     * Partially updates a feature toggle. Null parameters are skipped.
     *
     * @param id           the toggle identity
     * @param name         the new name, or {@code null} to keep current
     * @param description  the new description, or {@code null} to keep current
     * @param environments the new environment names, or {@code null} to keep current
     * @return the updated toggle
     * @throws com.homni.featuretoggle.domain.exception.ToggleNotFoundException if not found
     */
    FeatureToggle update(FeatureToggleId id, String name, String description, Set<String> environments);

    /**
     * Deletes a feature toggle.
     *
     * @param id the toggle identity
     * @throws com.homni.featuretoggle.domain.exception.ToggleNotFoundException if not found
     */
    void delete(FeatureToggleId id);
}
