package com.homni.featuretoggle.application.usecase;

import com.homni.featuretoggle.application.port.in.EnvironmentUseCase;
import com.homni.featuretoggle.application.port.out.EnvironmentRepositoryPort;
import com.homni.featuretoggle.domain.exception.EnvironmentInUseException;
import com.homni.featuretoggle.domain.exception.EnvironmentNotFoundException;
import com.homni.featuretoggle.domain.model.Environment;
import com.homni.featuretoggle.domain.model.EnvironmentId;

import java.util.List;

/**
 * Orchestrates deployment environment management operations.
 */
public class EnvironmentService implements EnvironmentUseCase {

    private final EnvironmentRepositoryPort environmentRepository;

    /**
     * Creates an environment service.
     *
     * @param environmentRepository the environment persistence port
     */
    public EnvironmentService(EnvironmentRepositoryPort environmentRepository) {
        this.environmentRepository = environmentRepository;
    }

    @Override
    public Environment create(String name) {
        Environment environment = new Environment(name);
        environmentRepository.save(environment);
        return environment;
    }

    @Override
    public List<Environment> listAll() {
        return environmentRepository.findAll();
    }

    @Override
    public void delete(EnvironmentId id) {
        Environment environment = environmentRepository.findById(id)
                .orElseThrow(() -> new EnvironmentNotFoundException(id));
        if (environmentRepository.isEnvironmentInUse(environment.name())) {
            throw new EnvironmentInUseException(environment.name());
        }
        environmentRepository.deleteById(id);
    }
}
