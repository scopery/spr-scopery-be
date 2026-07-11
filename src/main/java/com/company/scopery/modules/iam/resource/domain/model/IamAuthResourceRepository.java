package com.company.scopery.modules.iam.resource.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface IamAuthResourceRepository {
    IamAuthResource save(IamAuthResource resource);
    Optional<IamAuthResource> findById(UUID id);
    Optional<IamAuthResource> findByCodeAndResourceType(IamResourceCode code, IamResourceType resourceType);
    Optional<IamAuthResource> findByRefIdAndResourceType(UUID refId, IamResourceType resourceType);
    boolean existsByCodeAndResourceType(IamResourceCode code, IamResourceType resourceType);
    PageResult<IamAuthResource> findAll(String keyword, IamResourceType resourceType,
                                   IamResourceStatus status, PageQuery pageQuery);
    List<IamAuthResource> findAllByResourceTypeAndStatus(IamResourceType resourceType,
                                                         IamResourceStatus status);
    List<IamAuthResource> findAllByOrganizationId(UUID organizationId);
}
