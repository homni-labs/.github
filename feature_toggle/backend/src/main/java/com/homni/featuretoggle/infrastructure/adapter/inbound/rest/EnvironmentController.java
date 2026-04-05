package com.homni.featuretoggle.infrastructure.adapter.inbound.rest;

import com.homni.featuretoggle.application.port.in.EnvironmentUseCase;
import com.homni.featuretoggle.domain.model.Environment;
import com.homni.featuretoggle.domain.model.EnvironmentId;
import com.homni.featuretoggle.infrastructure.adapter.inbound.rest.presenter.ApiResponsePresenter;
import com.homni.generated.api.EnvironmentsApi;
import com.homni.generated.model.CreateEnvironmentRequest;
import com.homni.generated.model.EnvironmentListResponse;
import com.homni.generated.model.EnvironmentSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class EnvironmentController implements EnvironmentsApi {

    private final EnvironmentUseCase environmentUseCase;
    private final ApiResponsePresenter presenter;

    EnvironmentController(EnvironmentUseCase environmentUseCase, ApiResponsePresenter presenter) {
        this.environmentUseCase = environmentUseCase;
        this.presenter = presenter;
    }

    @Override
    public ResponseEntity<EnvironmentSingleResponse> createEnvironment(CreateEnvironmentRequest request) {
        Environment environment = environmentUseCase.create(request.getName());
        return ResponseEntity.ok(presenter.environment(environment));
    }

    @Override
    public ResponseEntity<EnvironmentListResponse> listEnvironments() {
        List<Environment> environments = environmentUseCase.listAll();
        return ResponseEntity.ok(presenter.environmentList(environments));
    }

    @Override
    public ResponseEntity<Void> deleteEnvironment(UUID environmentId) {
        environmentUseCase.delete(new EnvironmentId(environmentId));
        return ResponseEntity.noContent().build();
    }
}
