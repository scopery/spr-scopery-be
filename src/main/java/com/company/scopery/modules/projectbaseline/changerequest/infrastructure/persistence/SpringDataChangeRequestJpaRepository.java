package com.company.scopery.modules.projectbaseline.changerequest.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataChangeRequestJpaRepository extends JpaRepository<ChangeRequestJpaEntity, UUID> {
    List<ChangeRequestJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<ChangeRequestJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
