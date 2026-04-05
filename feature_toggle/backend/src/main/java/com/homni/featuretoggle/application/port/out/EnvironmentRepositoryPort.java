package com.homni.featuretoggle.application.port.out;

import com.homni.featuretoggle.domain.model.Environment;
import com.homni.featuretoggle.domain.model.EnvironmentId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for persisting deployment environments.
 */
public interface EnvironmentRepositoryPort {

    /**
     * Saves an environment (insert or update).
     *
     * @param environment the environment to save
     */
    void save(Environment environment);

    /**
     * Finds an environment by its identity.
     *
     * @param id the environment identity
     * @return the environment if found
     */
    Optional<Environment> findById(EnvironmentId id);

    /**
     * Returns all environments ordered by name.
     *
     * @return all environments
     */
    List<Environment> findAll();

    /**
     * Deletes an environment by its identity.
     *
     * @param id the environment identity
     */
    void deleteById(EnvironmentId id);

    /**
     * Checks whether any feature toggle references the given environment name.
     *
     * @param name the environment name to check
     * @return {@code true} if at least one toggle uses this environment
     */
    boolean isEnvironmentInUse(String name);
}
