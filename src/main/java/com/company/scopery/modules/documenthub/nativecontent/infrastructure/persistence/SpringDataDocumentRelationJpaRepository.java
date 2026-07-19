package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataDocumentRelationJpaRepository extends JpaRepository<DocumentRelationJpaEntity, UUID> {

    List<DocumentRelationJpaEntity> findBySourceDocumentId(UUID sourceDocumentId);
    List<DocumentRelationJpaEntity> findByTargetDocumentId(UUID targetDocumentId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM DocumentRelationJpaEntity e WHERE e.sourceDocumentId = :sourceDocumentId")
    int deleteBySourceDocumentId(@Param("sourceDocumentId") UUID sourceDocumentId);
}
