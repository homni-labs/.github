package com.homni.featuretoggle.infrastructure.config;

import com.homni.featuretoggle.application.port.in.ApiKeyUseCase;
import com.homni.featuretoggle.application.port.in.EnvironmentUseCase;
import com.homni.featuretoggle.application.port.in.FeatureToggleUseCase;
import com.homni.featuretoggle.application.port.in.UserUseCase;
import com.homni.featuretoggle.application.port.out.ApiKeyRepositoryPort;
import com.homni.featuretoggle.application.port.out.AppUserRepositoryPort;
import com.homni.featuretoggle.application.port.out.EnvironmentRepositoryPort;
import com.homni.featuretoggle.application.port.out.FeatureToggleRepositoryPort;
import com.homni.featuretoggle.application.usecase.ApiKeyService;
import com.homni.featuretoggle.application.usecase.EnvironmentService;
import com.homni.featuretoggle.application.usecase.FeatureToggleService;
import com.homni.featuretoggle.application.usecase.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompositionRootConfig {

    @Bean
    public FeatureToggleUseCase featureToggleUseCase(FeatureToggleRepositoryPort repo) {
        return new FeatureToggleService(repo);
    }

    @Bean
    public EnvironmentUseCase environmentUseCase(EnvironmentRepositoryPort repo) {
        return new EnvironmentService(repo);
    }

    @Bean
    public ApiKeyUseCase apiKeyUseCase(ApiKeyRepositoryPort repo) {
        return new ApiKeyService(repo);
    }

    @Bean
    public UserUseCase userUseCase(
            AppUserRepositoryPort repo,
            @Value("${app.oidc.default-admin-email:}") String defaultAdminEmail) {
        return new UserService(repo, defaultAdminEmail);
    }
}
