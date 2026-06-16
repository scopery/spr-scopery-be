package com.company.scopery.modules.iam.role.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IamRoleRepository {
    IamRole save(IamRole role);
    Optional<IamRole> findById(UUID id);
    boolean existsByCode(IamRoleCode code);
    boolean existsByCodeAndWorkspaceId(IamRoleCode code, UUID workspaceId);
    Page<IamRole> findAll(String keyword, UUID workspaceId, IamRoleScope roleScope,
                          IamRoleSource roleSource, IamRoleStatus status,
                          boolean includeDeleted, Pageable pageable);
}
