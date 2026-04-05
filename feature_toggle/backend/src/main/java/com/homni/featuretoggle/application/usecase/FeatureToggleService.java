package com.homni.featuretoggle.application.usecase;

import com.homni.featuretoggle.application.port.in.FeatureToggleUseCase;
import com.homni.featuretoggle.application.port.out.FeatureToggleRepositoryPort;
import com.homni.featuretoggle.domain.exception.ToggleNotFoundException;
import com.homni.featuretoggle.domain.model.FeatureToggle;
import com.homni.featuretoggle.domain.model.FeatureToggleId;
import com.homni.featuretoggle.domain.model.TogglePage;

import java.util.List;
import java.util.Set;

/**
 * Orchestrates feature toggle operations.
 */
public class FeatureToggleService implements FeatureToggleUseCase {

    private final FeatureToggleRepositoryPort toggleRepository;

    /**
     * Creates a feature toggle service.
     *
     * @param toggleRepository the toggle persistence port
     */
    public FeatureToggleService(FeatureToggleRepositoryPort toggleRepository) {
        this.toggleRepository = toggleRepository;
    }

    @Override
    public FeatureToggle create(String name, String description, Set<String> environments) {
        FeatureToggle toggle = new FeatureToggle(name, description, environments);
        toggleRepository.save(toggle);
        return toggle;
    }

    @Override
    public FeatureToggle findById(FeatureToggleId id) {
        return toggleRepository.findById(id)
                .orElseThrow(() -> new ToggleNotFoundException(id));
    }

    @Override
    public TogglePage list(Boolean enabled, String environment, int page, int size) {
        int offset = page * size;
        List<FeatureToggle> items = toggleRepository.findAll(enabled, environment, offset, size);
        long totalElements = toggleRepository.count(enabled, environment);
        return new TogglePage(items, totalElements);
    }

    @Override
    public FeatureToggle enable(FeatureToggleId id) {
        FeatureToggle toggle = findById(id);
        toggle.enable();
        toggleRepository.save(toggle);
        return toggle;
    }

    @Override
    public FeatureToggle disable(FeatureToggleId id) {
        FeatureToggle toggle = findById(id);
        toggle.disable();
        toggleRepository.save(toggle);
        return toggle;
    }

    @Override
    public FeatureToggle update(FeatureToggleId id, String name, String description,
                                Set<String> environments) {
        FeatureToggle toggle = findById(id);
        toggle.update(name, description, environments);
        toggleRepository.save(toggle);
        return toggle;
    }

    @Override
    public void delete(FeatureToggleId id) {
        findById(id);
        toggleRepository.deleteById(id);
    }
}
