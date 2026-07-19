package com.company.scopery.modules.knowledge.documenttype.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.knowledge.documenttype.application.action.ActivateDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.ArchiveDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.CreateDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.CreateSystemDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.CreateWorkspaceDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.DeactivateDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.SoftDeleteDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.UpdateDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.command.UpdateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.query.SearchDocumentTypeQuery;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.application.service.DocumentTypeQueryService;
import com.company.scopery.modules.knowledge.documenttype.http.request.CreateDocumentTypeRequest;
import com.company.scopery.modules.knowledge.documenttype.http.request.UpdateDocumentTypeRequest;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Knowledge - Document Types", description = "Manage document type catalog")
@RestController
@RequestMapping(KnowledgeApiPaths.DOCUMENT_TYPES)
public class DocumentTypeController {

    private final CreateDocumentTypeAction createAction;
    private final CreateSystemDocumentTypeAction createSystemAction;
    private final CreateWorkspaceDocumentTypeAction createWorkspaceAction;
    private final UpdateDocumentTypeAction updateAction;
    private final ActivateDocumentTypeAction activateAction;
    private final DeactivateDocumentTypeAction deactivateAction;
    private final ArchiveDocumentTypeAction archiveAction;
    private final SoftDeleteDocumentTypeAction softDeleteAction;
    private final DocumentTypeQueryService queryService;

    public DocumentTypeController(CreateDocumentTypeAction createAction,
                                  CreateSystemDocumentTypeAction createSystemAction,
                                  CreateWorkspaceDocumentTypeAction createWorkspaceAction,
                                  UpdateDocumentTypeAction updateAction,
                                  ActivateDocumentTypeAction activateAction,
                                  DeactivateDocumentTypeAction deactivateAction,
                                  ArchiveDocumentTypeAction archiveAction,
                                  SoftDeleteDocumentTypeAction softDeleteAction,
                                  DocumentTypeQueryService queryService) {
        this.createAction = createAction;
        this.createSystemAction = createSystemAction;
        this.createWorkspaceAction = createWorkspaceAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.archiveAction = archiveAction;
        this.softDeleteAction = softDeleteAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a document type (SYSTEM / ORGANIZATION / WORKSPACE)")
    @PostMapping
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> createDocumentType(
            @Valid @RequestBody CreateDocumentTypeRequest request) {
        DocumentTypeResponse response = createAction.execute(toCreateCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Create a system-scoped document type (legacy)")
    @PostMapping("/system")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> createSystemDocumentType(
            @Valid @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                createSystemAction.execute(toCreateCommand(request, "SYSTEM"))));
    }

    @Operation(summary = "Create a workspace-scoped document type (legacy)")
    @PostMapping("/workspace")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> createWorkspaceDocumentType(
            @Valid @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                createWorkspaceAction.execute(toCreateCommand(request, "WORKSPACE"))));
    }

    @Operation(summary = "Update document type")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> updateDocumentType(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDocumentTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                updateAction.execute(new UpdateDocumentTypeCommand(
                        id, request.name(), request.description(), request.category(),
                        request.defaultClassification(), request.defaultReviewCycleDays(),
                        request.defaultTemplateCode(), request.metadataSchemaJson()))));
    }

    @Operation(summary = "Get document type by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> getDocumentType(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getDocumentType(id)));
    }

    @Operation(summary = "Search document types")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DocumentTypeResponse>>> searchDocumentTypes(
            @Parameter(description = "Search by code or name") @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID workspaceId,
            @RequestParam(required = false) String documentScope,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean builtIn,
            @RequestParam(defaultValue = "false") boolean includeArchived,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<DocumentTypeResponse> result = queryService.searchDocumentTypes(new SearchDocumentTypeQuery(
                keyword, organizationId, workspaceId, documentScope, status, builtIn,
                includeArchived || includeDeleted, page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate document type")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> activateDocumentType(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(id)));
    }

    @Operation(summary = "Deactivate document type")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> deactivateDocumentType(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(deactivateAction.execute(id)));
    }

    @Operation(summary = "Archive document type")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> archiveDocumentType(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(archiveAction.execute(id)));
    }

    @Operation(summary = "Soft-delete document type (deprecated alias for archive)")
    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> softDeleteDocumentType(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(softDeleteAction.execute(id)));
    }

    private CreateDocumentTypeCommand toCreateCommand(CreateDocumentTypeRequest request) {
        return toCreateCommand(request, request.documentScope());
    }

    private CreateDocumentTypeCommand toCreateCommand(CreateDocumentTypeRequest request, String scope) {
        return new CreateDocumentTypeCommand(
                request.code(), request.name(), request.description(), scope,
                request.organizationId(), request.workspaceId(), request.category(),
                request.defaultClassification(), request.defaultReviewCycleDays(),
                request.defaultTemplateCode(), request.metadataSchemaJson());
    }
}
