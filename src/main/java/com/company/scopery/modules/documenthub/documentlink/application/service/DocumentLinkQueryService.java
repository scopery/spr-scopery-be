package com.company.scopery.modules.documenthub.documentlink.application.service;

import com.company.scopery.modules.documenthub.documentlink.application.response.DocumentLinkCountsResponse;
import com.company.scopery.modules.documenthub.documentlink.application.response.DocumentLinkListResponse;
import com.company.scopery.modules.documenthub.documentlink.application.response.DocumentLinkPageMeta;
import com.company.scopery.modules.documenthub.documentlink.application.response.DocumentLinkResponse;
import com.company.scopery.modules.documenthub.documentlink.domain.model.DocumentLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentLinkQueryService {

    private final DocumentLinkRepository linkRepository;

    public DocumentLinkQueryService(DocumentLinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Transactional(readOnly = true)
    public DocumentLinkListResponse byEntity(UUID workspaceId,
                                              String linkedEntityType,
                                              UUID linkedEntityId,
                                              UUID projectId,
                                              String relationType,
                                              boolean includeArchivedLinks,
                                              int limit,
                                              int offset) {
        List<DocumentLinkResponse> items = linkRepository
                .findByWorkspaceAndEntityFiltered(
                        workspaceId, linkedEntityType, linkedEntityId,
                        projectId, relationType, includeArchivedLinks, limit, offset)
                .stream()
                .map(DocumentLinkResponse::from)
                .toList();

        long total = linkRepository.countByWorkspaceAndEntityFiltered(
                workspaceId, linkedEntityType, linkedEntityId,
                projectId, relationType, includeArchivedLinks);

        return new DocumentLinkListResponse(items, new DocumentLinkPageMeta(limit, offset, total));
    }

    @Transactional(readOnly = true)
    public DocumentLinkCountsResponse linkCounts(UUID workspaceId, String documentIdsParam) {
        List<UUID> documentIds = parseUuidList(documentIdsParam);
        Map<UUID, Long> counts = linkRepository.countActiveLinksByDocumentIds(documentIds);
        return new DocumentLinkCountsResponse(counts);
    }

    @Transactional(readOnly = true)
    public DocumentLinkCountsResponse entityLinkCounts(UUID workspaceId,
                                                        String linkedEntityType,
                                                        UUID projectId,
                                                        String linkedEntityIdsParam) {
        List<UUID> entityIds = parseUuidList(linkedEntityIdsParam);
        Map<UUID, Long> counts = linkRepository.countActiveLinksByEntityIds(linkedEntityType, projectId, entityIds);
        return new DocumentLinkCountsResponse(counts);
    }

    private List<UUID> parseUuidList(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.isBlank()) {
            return List.of();
        }
        return Arrays.stream(commaSeparated.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }
}
