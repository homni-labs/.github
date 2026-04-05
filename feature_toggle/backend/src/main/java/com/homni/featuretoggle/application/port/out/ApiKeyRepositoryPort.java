package com.homni.featuretoggle.application.port.out;

import com.homni.featuretoggle.domain.model.ApiKey;
import com.homni.featuretoggle.domain.model.ApiKeyId;
import com.homni.featuretoggle.domain.model.TokenHash;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepositoryPort {

    void save(ApiKey apiKey);

    Optional<ApiKey> findById(ApiKeyId id);

    Optional<ApiKey> findByTokenHash(TokenHash tokenHash);

    List<ApiKey> findAll(int offset, int limit);

    long count();

    void deleteById(ApiKeyId id);
}
