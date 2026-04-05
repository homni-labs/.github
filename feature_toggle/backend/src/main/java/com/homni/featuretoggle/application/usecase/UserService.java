package com.homni.featuretoggle.application.usecase;

import com.homni.featuretoggle.application.port.in.UserUseCase;
import com.homni.featuretoggle.application.port.out.AppUserRepositoryPort;
import com.homni.featuretoggle.domain.exception.UserNotFoundException;
import com.homni.featuretoggle.domain.model.AppUser;
import com.homni.featuretoggle.domain.model.Role;
import com.homni.featuretoggle.domain.model.UserId;
import com.homni.featuretoggle.domain.model.UserPage;

import java.util.List;
import java.util.Optional;

public class UserService implements UserUseCase {

    private final AppUserRepositoryPort userRepository;
    private final String defaultAdminEmail;

    public UserService(AppUserRepositoryPort userRepository, String defaultAdminEmail) {
        this.userRepository = userRepository;
        this.defaultAdminEmail = defaultAdminEmail;
    }

    @Override
    public AppUser findOrCreateByOidcSubject(String oidcSubject, String email, String name) {
        Optional<AppUser> bySubject = userRepository.findByOidcSubject(oidcSubject);
        if (bySubject.isPresent()) {
            return bySubject.get();
        }

        Optional<AppUser> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent() && byEmail.get().canBindOidc()) {
            AppUser user = byEmail.get();
            user.bindOidcSubject(oidcSubject);
            userRepository.save(user);
            return user;
        }

        Role role = isDefaultAdmin(email) ? Role.ADMIN : Role.READER;
        AppUser user = new AppUser(oidcSubject, email, name, role);
        userRepository.save(user);
        return user;
    }

    @Override
    public AppUser findByOidcSubject(String oidcSubject) {
        return userRepository.findByOidcSubject(oidcSubject)
                .orElseThrow(() -> new UserNotFoundException(oidcSubject));
    }

    @Override
    public AppUser findById(UserId id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public UserPage list(int page, int size) {
        int offset = page * size;
        List<AppUser> items = userRepository.findAll(offset, size);
        long totalElements = userRepository.count();
        return new UserPage(items, totalElements);
    }

    @Override
    public AppUser changeRole(UserId id, Role role) {
        AppUser user = findById(id);
        user.changeRole(role);
        userRepository.save(user);
        return user;
    }

    @Override
    public AppUser disable(UserId id) {
        AppUser user = findById(id);
        user.disable();
        userRepository.save(user);
        return user;
    }

    @Override
    public AppUser activate(UserId id) {
        AppUser user = findById(id);
        user.activate();
        userRepository.save(user);
        return user;
    }

    @Override
    public AppUser updateUser(UserId id, Role role, Boolean active) {
        AppUser user = findById(id);
        if (role != null) {
            user.changeRole(role);
        }
        if (active != null && active) {
            user.activate();
        } else if (active != null) {
            user.disable();
        }
        userRepository.save(user);
        return user;
    }

    @Override
    public void delete(UserId id) {
        findById(id);
        userRepository.deleteById(id);
    }

    private boolean isDefaultAdmin(String email) {
        return defaultAdminEmail != null && !defaultAdminEmail.isBlank()
                && defaultAdminEmail.equalsIgnoreCase(email);
    }
}
