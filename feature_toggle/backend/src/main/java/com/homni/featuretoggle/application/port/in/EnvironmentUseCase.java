package com.homni.featuretoggle.application.port.in;

import com.homni.featuretoggle.domain.model.Environment;
import com.homni.featuretoggle.domain.model.EnvironmentId;

import java.util.List;

/**
 * Input port for deployment environment management operations.
 */
public interface EnvironmentUseCase {

    /**
     * Creates a new deployment environment.
     *
     * @param name the environment name
     * @return the created environment
     * @throws com.homni.featuretoggle.domain.exception.InvalidEnvironmentNameException if name is invalid
     * @throws com.homni.featuretoggle.domain.exception.EnvironmentAlreadyExistsException if name already exists
     */
    Environment create(String name);

    /**
     * Lists all deployment environments ordered by name.
     *
     * @return all environments
     */
    List<Environment> listAll();

    /**
     * Deletes a deployment environment.
     *
     * @param id the environment identity
     * @throws com.homni.featuretoggle.domain.exception.EnvironmentNotFoundException if not found
     * @throws com.homni.featuretoggle.domain.exception.EnvironmentInUseException if still referenced by toggles
     */
    void delete(EnvironmentId id);
}
