package com.company.scopery.modules.iam.user.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IamUserRepository {
    IamUser save(IamUser user);
    Optional<IamUser> findById(UUID id);
    Optional<IamUser> findByUsername(Username username);
    boolean existsByUsername(Username username);
    boolean existsByEmail(EmailAddress email);
    Page<IamUser> findAll(String keyword, IamUserStatus status, Pageable pageable);
}
