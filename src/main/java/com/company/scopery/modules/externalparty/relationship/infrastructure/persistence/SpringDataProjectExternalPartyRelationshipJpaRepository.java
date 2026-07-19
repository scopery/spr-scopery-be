package com.company.scopery.modules.externalparty.relationship.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataProjectExternalPartyRelationshipJpaRepository extends JpaRepository<ProjectExternalPartyRelationshipJpaEntity, UUID> {
    Optional<ProjectExternalPartyRelationshipJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ProjectExternalPartyRelationshipJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
