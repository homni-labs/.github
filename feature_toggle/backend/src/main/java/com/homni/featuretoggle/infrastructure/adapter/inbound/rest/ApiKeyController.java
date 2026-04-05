package com.homni.featuretoggle.infrastructure.adapter.inbound.rest;

import com.homni.featuretoggle.application.port.in.ApiKeyUseCase;
import com.homni.featuretoggle.domain.model.ApiKeyId;
import com.homni.featuretoggle.domain.model.ApiKeyPage;
import com.homni.featuretoggle.domain.model.IssuedApiKey;
import com.homni.featuretoggle.infrastructure.adapter.inbound.rest.presenter.ApiResponsePresenter;
import com.homni.generated.api.ApiKeysApi;
import com.homni.generated.model.ApiKeyCreatedSingleResponse;
import com.homni.generated.model.ApiKeyListResponse;
import com.homni.generated.model.CreateApiKeyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
public class ApiKeyController implements ApiKeysApi {

    private final ApiKeyUseCase apiKeyUseCase;
    private final ApiResponsePresenter presenter;

    ApiKeyController(ApiKeyUseCase apiKeyUseCase, ApiResponsePresenter presenter) {
        this.apiKeyUseCase = apiKeyUseCase;
        this.presenter = presenter;
    }

    @Override
    public ResponseEntity<ApiKeyListResponse> listApiKeys(Integer page, Integer size) {
        PaginationParams p = PaginationParams.of(page, size);
        ApiKeyPage result = apiKeyUseCase.list(p.page(), p.size());
        return ResponseEntity.ok(presenter.apiKeyPage(result, p.page(), p.size()));
    }

    @Override
    public ResponseEntity<ApiKeyCreatedSingleResponse> createApiKey(CreateApiKeyRequest request) {
        Instant expiresAt = request.getExpiresAt() != null ? request.getExpiresAt().toInstant() : null;
        IssuedApiKey issued = apiKeyUseCase.issue(request.getName(), expiresAt);
        return ResponseEntity.ok(presenter.apiKeyCreated(issued));
    }

    @Override
    public ResponseEntity<Void> revokeApiKey(UUID apiKeyId) {
        apiKeyUseCase.revoke(new ApiKeyId(apiKeyId));
        return ResponseEntity.noContent().build();
    }
}
