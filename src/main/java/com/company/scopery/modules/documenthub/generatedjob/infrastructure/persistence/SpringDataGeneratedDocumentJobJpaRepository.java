package com.company.scopery.modules.documenthub.generatedjob.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataGeneratedDocumentJobJpaRepository extends JpaRepository<GeneratedDocumentJobJpaEntity, UUID> {
    Optional<GeneratedDocumentJobJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<GeneratedDocumentJobJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
