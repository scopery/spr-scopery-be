package com.company.scopery.modules.externalparty.authority.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataProjectApprovalAuthorityJpaRepository extends JpaRepository<ProjectApprovalAuthorityJpaEntity, UUID> {
    Optional<ProjectApprovalAuthorityJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ProjectApprovalAuthorityJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
