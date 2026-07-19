package com.company.scopery.modules.quality.defectlink.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDefectLinkJpaRepository extends JpaRepository<DefectLinkJpaEntity, UUID> {
    Optional<DefectLinkJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DefectLinkJpaEntity> findByProjectIdAndDefectIdOrderByCreatedAtDesc(UUID projectId, UUID defectId);
}
