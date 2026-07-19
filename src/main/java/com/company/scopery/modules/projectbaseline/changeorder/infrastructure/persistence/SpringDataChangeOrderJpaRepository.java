package com.company.scopery.modules.projectbaseline.changeorder.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataChangeOrderJpaRepository extends JpaRepository<ChangeOrderJpaEntity, UUID> {
    List<ChangeOrderJpaEntity> findByChangeRequestIdOrderByCreatedAtDesc(UUID changeRequestId);
    Optional<ChangeOrderJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
