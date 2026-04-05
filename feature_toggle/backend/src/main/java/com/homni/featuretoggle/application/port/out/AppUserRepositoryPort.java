package com.homni.featuretoggle.application.port.out;

import com.homni.featuretoggle.domain.model.AppUser;
import com.homni.featuretoggle.domain.model.UserId;

import java.util.List;
import java.util.Optional;

public interface AppUserRepositoryPort {

    void save(AppUser user);

    Optional<AppUser> findById(UserId id);

    Optional<AppUser> findByOidcSubject(String oidcSubject);

    Optional<AppUser> findByEmail(String email);

    List<AppUser> findAll(int offset, int limit);

    long count();

    void deleteById(UserId id);
}
