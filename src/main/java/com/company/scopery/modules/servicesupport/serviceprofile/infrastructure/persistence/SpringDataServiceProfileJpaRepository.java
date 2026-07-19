package com.company.scopery.modules.servicesupport.serviceprofile.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataServiceProfileJpaRepository extends JpaRepository<ServiceProfileJpaEntity, UUID> {
    List<ServiceProfileJpaEntity> findByWorkspaceId(UUID workspaceId);
}
