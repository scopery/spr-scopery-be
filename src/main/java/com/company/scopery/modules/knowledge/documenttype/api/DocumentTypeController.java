package com.company.scopery.modules.knowledge.documenttype.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.knowledge.documenttype.api.request.CreateDocumentTypeRequest;
import com.company.scopery.modules.knowledge.documenttype.api.request.UpdateDocumentTypeRequest;
import com.company.scopery.modules.knowledge.documenttype.application.DocumentTypeApplicationService;
import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.command.UpdateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.query.SearchDocumentTypeQuery;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Knowledge - Document Types", description = "Manage document type configuration")
@RestController
@RequestMapping(KnowledgeApiPaths.DOCUMENT_TYPES)
public class DocumentTypeController {

    private final DocumentTypeApplicationService service;

    public DocumentTypeController(DocumentTypeApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create a system-scoped document type")
    @PostMapping("/system")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> createSystemDocumentType(
            @Valid @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                service.createSystemDocumentType(new CreateDocumentTypeCommand(
                        request.code(), request.name(), request.description(), "SYSTEM", null))));
    }

    @Operation(summary = "Create a workspace-scoped document type")
    @PostMapping("/workspace")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> createWorkspaceDocumentType(
            @Valid @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                service.createWorkspaceDocumentType(new CreateDocumentTypeCommand(
                        request.code(), request.name(), request.description(), "WORKSPACE", request.workspaceId()))));
    }

    @Operation(summary = "Update document type name and description")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> updateDocumentType(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDocumentTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                service.updateDocumentType(new UpdateDocumentTypeCommand(id, request.name(), request.description()))));
    }

    @Operation(summary = "Get document type by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> getDocumentType(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getDocumentType(id)));
    }

    @Operation(summary = "Search document types")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DocumentTypeResponse>>> searchDocumentTypes(
            @Parameter(description = "Search by code or name") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by workspace ID") @RequestParam(required = false) UUID workspaceId,
            @Parameter(description = "Filter by scope (SYSTEM, WORKSPACE)") @RequestParam(required = false) String documentScope,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DELETED)") @RequestParam(required = false) String status,
            @Parameter(description = "Include soft-deleted types") @RequestParam(defaultValue = "false") boolean includeDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                service.searchDocumentTypes(new SearchDocumentTypeQuery(
                        keyword, workspaceId, documentScope, status, includeDeleted, page, size)))));
    }

    @Operation(summary = "Activate document type")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> activateDocumentType(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.activateDocumentType(id)));
    }

    @Operation(summary = "Deactivate document type")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> deactivateDocumentType(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.deactivateDocumentType(id)));
    }

    @Operation(summary = "Soft-delete a document type")
    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> softDeleteDocumentType(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.softDeleteDocumentType(id)));
    }
}
