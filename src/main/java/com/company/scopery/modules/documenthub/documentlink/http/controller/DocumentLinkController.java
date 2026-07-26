package com.company.scopery.modules.documenthub.documentlink.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.documentlink.application.action.BulkCreateDocumentLinksAction;
import com.company.scopery.modules.documenthub.documentlink.application.command.BulkCreateDocumentLinksCommand;
import com.company.scopery.modules.documenthub.documentlink.application.response.BulkCreateDocumentLinksResult;
import com.company.scopery.modules.documenthub.documentlink.application.response.DocumentLinkCountsResponse;
import com.company.scopery.modules.documenthub.documentlink.application.response.DocumentLinkListResponse;
import com.company.scopery.modules.documenthub.documentlink.application.service.DocumentLinkQueryService;
import com.company.scopery.modules.documenthub.documentlink.domain.enums.DocumentLinkEntityType;
import com.company.scopery.modules.documenthub.documentlink.domain.enums.DocumentLinkRelationType;
import com.company.scopery.modules.documenthub.documentlink.http.request.BulkCreateDocumentLinksRequest;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Document Hub - Document Links")
public class DocumentLinkController {

    private final DocumentLinkQueryService query;
    private final BulkCreateDocumentLinksAction bulkCreate;

    public DocumentLinkController(DocumentLinkQueryService query,
                                   BulkCreateDocumentLinksAction bulkCreate) {
        this.query = query;
        this.bulkCreate = bulkCreate;
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_LINKS + "/by-entity")
    @Operation(summary = "List document links for a given entity")
    public ApiResponse<DocumentLinkListResponse> byEntity(
            @PathVariable UUID workspaceId,
            @RequestParam(name = "linked_entity_type") String linkedEntityType,
            @RequestParam(name = "linked_entity_id") UUID linkedEntityId,
            @RequestParam(name = "project_id", required = false) UUID projectId,
            @RequestParam(name = "relation_type", required = false) String relationType,
            @RequestParam(name = "include_archived_links", defaultValue = "false") boolean includeArchivedLinks,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ApiResponse.success(query.byEntity(
                workspaceId, linkedEntityType, linkedEntityId,
                projectId, relationType, includeArchivedLinks, limit, offset));
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_LINKS + "/link-counts")
    @Operation(summary = "Get active link counts for given document IDs")
    public ApiResponse<DocumentLinkCountsResponse> linkCounts(
            @PathVariable UUID workspaceId,
            @RequestParam(name = "document_ids") String documentIds) {
        return ApiResponse.success(query.linkCounts(workspaceId, documentIds));
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_LINKS + "/entity-link-counts")
    @Operation(summary = "Get active link counts for given entity IDs")
    public ApiResponse<DocumentLinkCountsResponse> entityLinkCounts(
            @PathVariable UUID workspaceId,
            @RequestParam(name = "linked_entity_type") String linkedEntityType,
            @RequestParam(name = "project_id") UUID projectId,
            @RequestParam(name = "linked_entity_ids") String linkedEntityIds) {
        return ApiResponse.success(query.entityLinkCounts(workspaceId, linkedEntityType, projectId, linkedEntityIds));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_LINKS + "/bulk")
    @Operation(summary = "Bulk create document links")
    public ApiResponse<BulkCreateDocumentLinksResult> bulkCreate(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody BulkCreateDocumentLinksRequest request) {
        DocumentLinkEntityType entityType = DocumentLinkEntityType.fromString(request.linkedEntityType());
        DocumentLinkRelationType relationType = DocumentLinkRelationType.fromString(request.relationType());
        return ApiResponse.success(bulkCreate.execute(new BulkCreateDocumentLinksCommand(
                workspaceId,
                request.projectId(),
                entityType,
                request.linkedEntityId(),
                relationType,
                request.documentIds()
        )));
    }
}
