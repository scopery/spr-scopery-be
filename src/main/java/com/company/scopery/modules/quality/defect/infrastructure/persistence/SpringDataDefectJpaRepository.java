package com.company.scopery.modules.quality.defect.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface SpringDataDefectJpaRepository extends JpaRepository<DefectJpaEntity, UUID> {
    Optional<DefectJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DefectJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    @Query("select d from DefectJpaEntity d where d.projectId = :projectId and d.severity in ('BLOCKER','CRITICAL') and d.status not in ('CLOSED','REJECTED','ARCHIVED','VERIFIED')")
    List<DefectJpaEntity> findOpenBlockers(@Param("projectId") UUID projectId);
}
