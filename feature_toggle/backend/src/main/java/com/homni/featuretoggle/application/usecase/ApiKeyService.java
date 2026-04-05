package com.homni.featuretoggle.application.usecase;

import com.homni.featuretoggle.application.port.in.ApiKeyUseCase;
import com.homni.featuretoggle.application.port.out.ApiKeyRepositoryPort;
import com.homni.featuretoggle.domain.exception.ApiKeyNotFoundException;
import com.homni.featuretoggle.domain.model.ApiKey;
import com.homni.featuretoggle.domain.model.ApiKeyId;
import com.homni.featuretoggle.domain.model.ApiKeyPage;
import com.homni.featuretoggle.domain.model.IssuedApiKey;

import java.time.Instant;
import java.util.List;

public class ApiKeyService implements ApiKeyUseCase {

    private final ApiKeyRepositoryPort apiKeyRepository;

    public ApiKeyService(ApiKeyRepositoryPort apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public IssuedApiKey issue(String name, Instant expiresAt) {
        IssuedApiKey issued = new IssuedApiKey(name, expiresAt);
        apiKeyRepository.save(issued.apiKey);
        return issued;
    }

    @Override
    public ApiKeyPage list(int page, int size) {
        int offset = page * size;
        List<ApiKey> items = apiKeyRepository.findAll(offset, size);
        long totalElements = apiKeyRepository.count();
        return new ApiKeyPage(items, totalElements);
    }

    @Override
    public void revoke(ApiKeyId id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ApiKeyNotFoundException(id));
        apiKey.revoke();
        apiKeyRepository.save(apiKey);
    }
}
