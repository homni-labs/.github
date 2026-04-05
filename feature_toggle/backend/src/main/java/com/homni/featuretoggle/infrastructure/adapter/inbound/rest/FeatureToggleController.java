package com.homni.featuretoggle.infrastructure.adapter.inbound.rest;

import com.homni.featuretoggle.application.port.in.FeatureToggleUseCase;
import com.homni.featuretoggle.domain.model.FeatureToggle;
import com.homni.featuretoggle.domain.model.FeatureToggleId;
import com.homni.featuretoggle.domain.model.TogglePage;
import com.homni.featuretoggle.infrastructure.adapter.inbound.rest.presenter.ApiResponsePresenter;
import com.homni.generated.api.TogglesApi;
import com.homni.generated.model.CreateFeatureToggleRequest;
import com.homni.generated.model.FeatureToggleListResponse;
import com.homni.generated.model.FeatureToggleSingleResponse;
import com.homni.generated.model.UpdateFeatureToggleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class FeatureToggleController implements TogglesApi {

    private final FeatureToggleUseCase toggleUseCase;
    private final ApiResponsePresenter presenter;

    FeatureToggleController(FeatureToggleUseCase toggleUseCase, ApiResponsePresenter presenter) {
        this.toggleUseCase = toggleUseCase;
        this.presenter = presenter;
    }

    @Override
    public ResponseEntity<FeatureToggleListResponse> listToggles(Boolean enabled, String environment,
                                                                  Integer page, Integer size) {
        PaginationParams p = PaginationParams.of(page, size);
        TogglePage result = toggleUseCase.list(enabled, environment, p.page(), p.size());
        return ResponseEntity.ok(presenter.togglePage(result, p.page(), p.size()));
    }

    @Override
    public ResponseEntity<FeatureToggleSingleResponse> getToggle(UUID toggleId) {
        return ResponseEntity.ok(presenter.toggle(toggleUseCase.findById(new FeatureToggleId(toggleId))));
    }

    @Override
    public ResponseEntity<FeatureToggleSingleResponse> createToggle(CreateFeatureToggleRequest request) {
        Set<String> environments = toStringSet(request.getEnvironments());
        FeatureToggle toggle = toggleUseCase.create(request.getName(), request.getDescription(), environments);
        return ResponseEntity.ok(presenter.toggle(toggle));
    }

    @Override
    public ResponseEntity<FeatureToggleSingleResponse> updateToggle(UUID toggleId,
                                                                     UpdateFeatureToggleRequest request) {
        Set<String> environments = toStringSet(request.getEnvironments());
        FeatureToggle toggle = toggleUseCase.update(
                new FeatureToggleId(toggleId),
                request.getName(), request.getDescription(), environments);
        return ResponseEntity.ok(presenter.toggle(toggle));
    }

    @Override
    public ResponseEntity<FeatureToggleSingleResponse> enableToggle(UUID toggleId) {
        return ResponseEntity.ok(presenter.toggle(toggleUseCase.enable(new FeatureToggleId(toggleId))));
    }

    @Override
    public ResponseEntity<FeatureToggleSingleResponse> disableToggle(UUID toggleId) {
        return ResponseEntity.ok(presenter.toggle(toggleUseCase.disable(new FeatureToggleId(toggleId))));
    }

    @Override
    public ResponseEntity<Void> deleteToggle(UUID toggleId) {
        toggleUseCase.delete(new FeatureToggleId(toggleId));
        return ResponseEntity.noContent().build();
    }

    private Set<String> toStringSet(List<String> list) {
        if (list == null) {
            return null;
        }
        return new LinkedHashSet<>(list);
    }
}
