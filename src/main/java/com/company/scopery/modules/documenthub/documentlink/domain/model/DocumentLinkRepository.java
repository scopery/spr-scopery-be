package com.company.scopery.modules.documenthub.documentlink.domain.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DocumentLinkRepository {

    DocumentLink save(DocumentLink link);

    List<DocumentLink> saveAll(List<DocumentLink> links);

    boolean existsByDocumentIdAndTargetTypeAndTargetIdAndLinkType(
            UUID documentId, String targetType, UUID targetId, String linkType);

    List<DocumentLink> findByWorkspaceAndEntityFiltered(
            UUID workspaceId,
            String linkedEntityType,
            UUID linkedEntityId,
            UUID projectId,
            String relationType,
            boolean includeArchived,
            int limit,
            int offset);

    long countByWorkspaceAndEntityFiltered(
            UUID workspaceId,
            String linkedEntityType,
            UUID linkedEntityId,
            UUID projectId,
            String relationType,
            boolean includeArchived);

    Map<UUID, Long> countActiveLinksByDocumentIds(List<UUID> documentIds);

    Map<UUID, Long> countActiveLinksByEntityIds(String linkedEntityType, UUID projectId, List<UUID> entityIds);
}
