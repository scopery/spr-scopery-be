package com.company.scopery.modules.iam.resource.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IamAuthResourceRepository {
    IamAuthResource save(IamAuthResource resource);
    Optional<IamAuthResource> findById(UUID id);
    Optional<IamAuthResource> findByRefIdAndResourceType(UUID refId, IamResourceType resourceType);
    boolean existsByCodeAndResourceType(IamResourceCode code, IamResourceType resourceType);
    Page<IamAuthResource> findAll(String keyword, IamResourceType resourceType,
                                   IamResourceStatus status, Pageable pageable);
}
