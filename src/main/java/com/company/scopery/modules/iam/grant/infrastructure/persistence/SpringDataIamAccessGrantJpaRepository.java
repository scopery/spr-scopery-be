package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataIamAccessGrantJpaRepository
        extends JpaRepository<IamAccessGrantJpaEntity, UUID>,
                JpaSpecificationExecutor<IamAccessGrantJpaEntity> {

    boolean existsBySubjectIdAndResourceId(UUID subjectId, UUID resourceId);

    @Query("""
            SELECT g FROM IamAccessGrantJpaEntity g
            WHERE g.status = 'ACTIVE'
              AND g.subjectType IN :subjectTypes
              AND g.subjectId IN :subjectIds
              AND g.resourceId = :resourceId
            """)
    List<IamAccessGrantJpaEntity> findActiveBySubjectsAndResource(
            @Param("subjectTypes") List<String> subjectTypes,
            @Param("subjectIds") List<UUID> subjectIds,
            @Param("resourceId") UUID resourceId);
}
