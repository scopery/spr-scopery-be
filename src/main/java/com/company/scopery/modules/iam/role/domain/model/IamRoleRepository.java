package com.company.scopery.modules.iam.role.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleStatus;
import com.company.scopery.modules.iam.role.domain.valueobject.IamRoleCode;

import java.util.Optional;
import java.util.UUID;

public interface IamRoleRepository {
    IamRole save(IamRole role);
    Optional<IamRole> findById(UUID id);
    Optional<IamRole> findByCode(IamRoleCode code);
    boolean existsByCode(IamRoleCode code);
    boolean existsByCodeAndWorkspaceId(IamRoleCode code, UUID workspaceId);
    PageResult<IamRole> findAll(String keyword, UUID workspaceId, IamRoleScope roleScope,
                          IamRoleSource roleSource, IamRoleStatus status,
                          boolean includeDeleted, PageQuery pageQuery);
}
