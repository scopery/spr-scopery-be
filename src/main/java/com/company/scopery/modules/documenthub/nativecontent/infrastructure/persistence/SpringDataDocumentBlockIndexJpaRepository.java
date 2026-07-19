package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataDocumentBlockIndexJpaRepository extends JpaRepository<DocumentBlockIndexJpaEntity, UUID> {

    List<DocumentBlockIndexJpaEntity> findByDocumentIdOrderByOrdinalAsc(UUID documentId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM DocumentBlockIndexJpaEntity e WHERE e.documentId = :documentId")
    int deleteByDocumentId(@Param("documentId") UUID documentId);
}
