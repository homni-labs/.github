package com.homni.featuretoggle.infrastructure.adapter.inbound.rest;

import com.homni.featuretoggle.application.port.in.UserUseCase;
import com.homni.featuretoggle.domain.model.AppUser;
import com.homni.featuretoggle.domain.model.Role;
import com.homni.featuretoggle.domain.model.UserId;
import com.homni.featuretoggle.domain.model.UserPage;
import com.homni.featuretoggle.infrastructure.adapter.inbound.rest.presenter.ApiResponsePresenter;
import com.homni.generated.api.UsersApi;
import com.homni.generated.model.UpdateUserRequest;
import com.homni.generated.model.UserListResponse;
import com.homni.generated.model.UserSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController implements UsersApi {

    private final UserUseCase userUseCase;
    private final ApiResponsePresenter presenter;

    UserController(UserUseCase userUseCase, ApiResponsePresenter presenter) {
        this.userUseCase = userUseCase;
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<UserSingleResponse> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new AccessDeniedException("Only OIDC users can access /users/me");
        }
        String subject = jwtAuth.getToken().getSubject();
        return ResponseEntity.ok(presenter.user(userUseCase.findByOidcSubject(subject)));
    }

    @Override
    public ResponseEntity<UserListResponse> listUsers(Integer page, Integer size) {
        PaginationParams p = PaginationParams.of(page, size);
        UserPage result = userUseCase.list(p.page(), p.size());
        return ResponseEntity.ok(presenter.userPage(result, p.page(), p.size()));
    }

    @Override
    public ResponseEntity<UserSingleResponse> getUser(UUID userId) {
        return ResponseEntity.ok(presenter.user(userUseCase.findById(new UserId(userId))));
    }

    @Override
    public ResponseEntity<UserSingleResponse> updateUser(UUID userId, UpdateUserRequest request) {
        Role role = request.getRole() != null ? Role.valueOf(request.getRole().name()) : null;
        AppUser user = userUseCase.updateUser(new UserId(userId), role, request.getActive());
        return ResponseEntity.ok(presenter.user(user));
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID userId) {
        userUseCase.delete(new UserId(userId));
        return ResponseEntity.noContent().build();
    }
}
