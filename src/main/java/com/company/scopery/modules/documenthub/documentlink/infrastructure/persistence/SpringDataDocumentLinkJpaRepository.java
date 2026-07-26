package com.company.scopery.modules.documenthub.documentlink.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataDocumentLinkJpaRepository extends JpaRepository<DocumentLinkJpaEntity, UUID> {

    boolean existsByDocumentIdAndTargetTypeAndTargetIdAndLinkType(
            UUID documentId, String targetType, UUID targetId, String linkType);
}
