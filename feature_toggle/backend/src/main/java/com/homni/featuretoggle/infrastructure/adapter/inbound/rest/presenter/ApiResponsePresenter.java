package com.homni.featuretoggle.infrastructure.adapter.inbound.rest.presenter;

import com.homni.featuretoggle.domain.model.ApiKeyPage;
import com.homni.featuretoggle.domain.model.AppUser;
import com.homni.featuretoggle.domain.model.Environment;
import com.homni.featuretoggle.domain.model.IssuedApiKey;
import com.homni.featuretoggle.domain.model.TogglePage;
import com.homni.featuretoggle.domain.model.UserPage;
import com.homni.generated.model.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class ApiResponsePresenter {

    public FeatureToggleSingleResponse toggle(com.homni.featuretoggle.domain.model.FeatureToggle toggle) {
        return new FeatureToggleSingleResponse(toToggleDto(toggle), meta());
    }

    public FeatureToggleListResponse togglePage(TogglePage page, int pageNum, int pageSize) {
        List<FeatureToggle> items = page.items().stream().map(this::toToggleDto).toList();

        FeatureToggleListResponse response = new FeatureToggleListResponse();
        response.setPayload(items);
        response.setPagination(buildPagination(page.totalElements(), pageNum, pageSize));
        response.setMeta(meta());
        return response;
    }

    public ApiKeyListResponse apiKeyPage(ApiKeyPage page, int pageNum, int pageSize) {
        List<ApiKey> items = page.items().stream().map(this::toApiKeyDto).toList();

        ApiKeyListResponse response = new ApiKeyListResponse();
        response.setPayload(items);
        response.setPagination(buildPagination(page.totalElements(), pageNum, pageSize));
        response.setMeta(meta());
        return response;
    }

    public ApiKeyCreatedSingleResponse apiKeyCreated(IssuedApiKey issued) {
        com.homni.featuretoggle.domain.model.ApiKey key = issued.apiKey;
        ApiKeyCreated dto = new ApiKeyCreated()
                .id(key.id.value)
                .name(key.name)
                .rawToken(issued.rawToken)
                .createdAt(toUtc(key.createdAt))
                .expiresAt(toUtc(key.expiresAt));
        return new ApiKeyCreatedSingleResponse(dto, meta());
    }

    public UserSingleResponse user(AppUser user) {
        return new UserSingleResponse(toUserDto(user), meta());
    }

    public UserListResponse userPage(UserPage page, int pageNum, int pageSize) {
        List<User> items = page.items().stream().map(this::toUserDto).toList();

        UserListResponse response = new UserListResponse();
        response.setPayload(items);
        response.setPagination(buildPagination(page.totalElements(), pageNum, pageSize));
        response.setMeta(meta());
        return response;
    }

    /**
     * Presents a single environment as API response.
     *
     * @param env the domain environment
     * @return the API response DTO
     */
    public EnvironmentSingleResponse environment(Environment env) {
        return new EnvironmentSingleResponse(toEnvironmentDto(env), meta());
    }

    /**
     * Presents a list of environments as API response.
     *
     * @param environments the domain environments
     * @return the API response DTO
     */
    public EnvironmentListResponse environmentList(List<Environment> environments) {
        List<com.homni.generated.model.Environment> items = environments.stream()
                .map(this::toEnvironmentDto).toList();
        EnvironmentListResponse response = new EnvironmentListResponse();
        response.setPayload(items);
        response.setMeta(meta());
        return response;
    }

    private com.homni.generated.model.Environment toEnvironmentDto(Environment e) {
        return new com.homni.generated.model.Environment()
                .id(e.id.value)
                .name(e.name())
                .createdAt(toUtc(e.createdAt));
    }

    private FeatureToggle toToggleDto(com.homni.featuretoggle.domain.model.FeatureToggle t) {
        List<String> envs = t.environments().stream().toList();
        return new FeatureToggle()
                .id(t.id.value)
                .name(t.name())
                .description(t.description())
                .enabled(t.isEnabled())
                .environments(envs)
                .createdAt(toUtc(t.createdAt))
                .updatedAt(toUtc(t.lastModifiedAt()));
    }

    private ApiKey toApiKeyDto(com.homni.featuretoggle.domain.model.ApiKey k) {
        return new ApiKey()
                .id(k.id.value)
                .name(k.name)
                .maskedToken(k.maskedToken())
                .active(k.isActive())
                .createdAt(toUtc(k.createdAt))
                .expiresAt(toUtc(k.expiresAt));
    }

    private User toUserDto(AppUser u) {
        return new User()
                .id(u.id.value)
                .oidcSubject(u.oidcSubject())
                .email(u.email.value())
                .name(u.displayName())
                .role(User.RoleEnum.fromValue(u.currentRole().name()))
                .active(u.isActive())
                .createdAt(toUtc(u.createdAt))
                .updatedAt(toUtc(u.lastModifiedAt()));
    }

    private Pagination buildPagination(long totalElements, int pageNum, int pageSize) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return new Pagination()
                .page(pageNum)
                .size(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages);
    }

    private OffsetDateTime toUtc(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

    private ResponseMeta meta() {
        return new ResponseMeta().timestamp(OffsetDateTime.now(ZoneOffset.UTC));
    }
}
