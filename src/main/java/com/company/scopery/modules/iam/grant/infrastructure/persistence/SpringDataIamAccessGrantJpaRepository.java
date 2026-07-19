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

    List<IamAccessGrantJpaEntity> findAllByResourceIdAndStatus(UUID resourceId, String status);

    List<IamAccessGrantJpaEntity> findAllBySubjectTypeAndSubjectIdAndResourceIdAndStatus(
            String subjectType, UUID subjectId, UUID resourceId, String status);

    @Query("""
            SELECT COUNT(g) > 0 FROM IamAccessGrantJpaEntity g
            WHERE g.subjectId IN :subjectIds
              AND g.scopeType = 'GLOBAL_RESOURCE'
              AND g.effect = 'ALLOW'
              AND g.status = 'ACTIVE'
            """)
    boolean existsActiveGlobalGrantForSubjects(@Param("subjectIds") List<UUID> subjectIds);
}
