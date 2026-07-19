package com.company.scopery.modules.resourcecapacity.projectallocation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SpringDataProjectResourceAllocationJpaRepository
        extends JpaRepository<ProjectResourceAllocationJpaEntity, UUID>,
        JpaSpecificationExecutor<ProjectResourceAllocationJpaEntity> {

    List<ProjectResourceAllocationJpaEntity> findByUserIdAndStatus(UUID userId, String status);

    List<ProjectResourceAllocationJpaEntity> findByProjectIdAndStatus(UUID projectId, String status);
}
