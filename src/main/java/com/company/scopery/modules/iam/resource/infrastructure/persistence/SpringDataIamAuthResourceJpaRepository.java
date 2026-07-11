package com.company.scopery.modules.iam.resource.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface SpringDataIamAuthResourceJpaRepository
        extends JpaRepository<IamAuthResourceJpaEntity, UUID>,
                JpaSpecificationExecutor<IamAuthResourceJpaEntity> {

    boolean existsByCodeAndResourceType(String code, String resourceType);

    Optional<IamAuthResourceJpaEntity> findByCodeAndResourceType(String code, String resourceType);

    Optional<IamAuthResourceJpaEntity> findByRefIdAndResourceType(UUID refId, String resourceType);

    List<IamAuthResourceJpaEntity> findAllByResourceTypeAndStatus(String resourceType, String status);
    List<IamAuthResourceJpaEntity> findAllByOrganizationId(UUID organizationId);
}
