package com.company.scopery.modules.iam.permission.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.permission.domain.enums.IamDataAccessPolicy;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionAssignableSubjectType;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionCategory;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionRiskLevel;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;
import com.company.scopery.modules.iam.permission.domain.enums.IamResourceScopeLevel;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;

import java.util.Optional;
import java.util.UUID;

public interface IamPermissionRepository {

    IamPermission save(IamPermission permission);

    Optional<IamPermission> findById(UUID id);

    Optional<IamPermission> findByCode(IamPermissionCode code);

    boolean existsByCode(IamPermissionCode code);

    PageResult<IamPermission> findAll(String keyword, String moduleCode,
                                IamResourceScopeLevel resourceScopeLevel,
                                IamDataAccessPolicy dataAccessPolicy,
                                IamPermissionCategory permissionCategory,
                                IamPermissionRiskLevel riskLevel,
                                IamPermissionAssignableSubjectType assignableSubjectType,
                                IamPermissionStatus status,
                                PageQuery pageQuery);
}
