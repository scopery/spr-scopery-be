package com.company.scopery.modules.iam.user.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;

import java.util.Optional;
import java.util.UUID;

public interface IamUserRepository {
    IamUser save(IamUser user);
    Optional<IamUser> findById(UUID id);
    Optional<IamUser> findByUsername(Username username);
    Optional<IamUser> findByEmail(EmailAddress email);
    boolean existsByUsername(Username username);
    boolean existsByEmail(EmailAddress email);
    PageResult<IamUser> findAll(String keyword, IamUserStatus status, PageQuery pageQuery);
}
