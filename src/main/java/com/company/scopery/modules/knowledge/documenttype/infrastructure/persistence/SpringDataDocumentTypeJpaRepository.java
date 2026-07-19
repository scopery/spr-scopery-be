package com.company.scopery.modules.knowledge.documenttype.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpringDataDocumentTypeJpaRepository
        extends JpaRepository<DocumentTypeJpaEntity, UUID>, JpaSpecificationExecutor<DocumentTypeJpaEntity> {

    @Query("""
            SELECT COUNT(d) > 0 FROM DocumentTypeJpaEntity d
            WHERE d.code = :code
              AND d.documentScope = 'SYSTEM'
              AND d.organizationId IS NULL
              AND d.workspaceId IS NULL
              AND d.archivedAt IS NULL
              AND d.deletedAt IS NULL
            """)
    boolean existsByCodeAndScopeSystem(@Param("code") String code);

    @Query("""
            SELECT d FROM DocumentTypeJpaEntity d
            WHERE d.code = :code
              AND d.documentScope = 'SYSTEM'
              AND d.organizationId IS NULL
              AND d.workspaceId IS NULL
              AND d.archivedAt IS NULL
              AND d.deletedAt IS NULL
            """)
    java.util.Optional<DocumentTypeJpaEntity> findByCodeAndScopeSystem(@Param("code") String code);

    @Query("""
            SELECT COUNT(d) > 0 FROM DocumentTypeJpaEntity d
            WHERE d.code = :code
              AND d.organizationId = :organizationId
              AND d.documentScope = 'ORGANIZATION'
              AND d.workspaceId IS NULL
              AND d.archivedAt IS NULL
              AND d.deletedAt IS NULL
            """)
    boolean existsByCodeAndOrganizationId(@Param("code") String code,
                                          @Param("organizationId") UUID organizationId);

    @Query("""
            SELECT COUNT(d) > 0 FROM DocumentTypeJpaEntity d
            WHERE d.code = :code
              AND d.workspaceId = :workspaceId
              AND d.archivedAt IS NULL
              AND d.deletedAt IS NULL
            """)
    boolean existsByCodeAndWorkspaceId(@Param("code") String code, @Param("workspaceId") UUID workspaceId);
}
