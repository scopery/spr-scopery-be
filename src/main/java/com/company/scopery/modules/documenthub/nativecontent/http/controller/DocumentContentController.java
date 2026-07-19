package com.company.scopery.modules.documenthub.nativecontent.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.nativecontent.application.action.RestoreRevisionAction;
import com.company.scopery.modules.documenthub.nativecontent.application.action.SaveDocumentContentAction;
import com.company.scopery.modules.documenthub.nativecontent.application.command.RestoreRevisionCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.command.SaveDocumentContentCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentContentResponse;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentRevisionResponse;
import com.company.scopery.modules.documenthub.nativecontent.application.service.DocumentContentQueryService;
import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;
import com.company.scopery.modules.documenthub.nativecontent.http.request.SaveDocumentContentRequest;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Document Hub - Native Content")
public class DocumentContentController {

    private final SaveDocumentContentAction saveContent;
    private final RestoreRevisionAction restoreRevision;
    private final DocumentContentQueryService query;

    public DocumentContentController(SaveDocumentContentAction saveContent,
                                      RestoreRevisionAction restoreRevision,
                                      DocumentContentQueryService query) {
        this.saveContent = saveContent;
        this.restoreRevision = restoreRevision;
        this.query = query;
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_CONTENT)
    @Operation(summary = "Get native document content")
    public ApiResponse<DocumentContentResponse> getContent(@PathVariable UUID projectId,
                                                            @PathVariable UUID documentId) {
        return ApiResponse.success(query.getContent(projectId, documentId));
    }

    @PutMapping(DocumentHubApiPaths.DOCUMENT_CONTENT)
    @Operation(summary = "Save native document content (optimistic lock)")
    public ApiResponse<DocumentContentResponse> saveContent(@PathVariable UUID projectId,
                                                             @PathVariable UUID documentId,
                                                             @Valid @RequestBody SaveDocumentContentRequest r) {
        RevisionType revisionType = parseRevisionType(r.revisionType());
        return ApiResponse.success(saveContent.execute(new SaveDocumentContentCommand(
                projectId, documentId, r.ast(), r.expectedBaseRevisionNo(), r.schemaVersion(), revisionType)));
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_REVISIONS)
    @Operation(summary = "List document revisions (no AST, newest first)")
    public ApiResponse<PageResponse<DocumentRevisionResponse>> listRevisions(@PathVariable UUID projectId,
                                                                              @PathVariable UUID documentId,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(query.listRevisions(projectId, documentId, page, size));
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_REVISIONS + "/{revisionNo}")
    @Operation(summary = "Get specific revision with full AST")
    public ApiResponse<DocumentRevisionResponse> getRevision(@PathVariable UUID projectId,
                                                              @PathVariable UUID documentId,
                                                              @PathVariable long revisionNo) {
        return ApiResponse.success(query.getRevision(projectId, documentId, revisionNo));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_REVISIONS + "/{revisionNo}/restore")
    @Operation(summary = "Restore document to a previous revision")
    public ApiResponse<DocumentContentResponse> restoreRevision(@PathVariable UUID projectId,
                                                                 @PathVariable UUID documentId,
                                                                 @PathVariable long revisionNo) {
        return ApiResponse.success(restoreRevision.execute(new RestoreRevisionCommand(projectId, documentId, revisionNo)));
    }

    private RevisionType parseRevisionType(String value) {
        if (value == null || value.isBlank()) return RevisionType.MANUAL;
        try {
            return RevisionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RevisionType.MANUAL;
        }
    }
}
