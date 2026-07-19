package com.company.scopery.modules.documenthub.document.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.document.application.action.*;
import com.company.scopery.modules.documenthub.document.application.command.ApproveDocumentCommand;
import com.company.scopery.modules.documenthub.document.application.command.CreateDocumentCommand;
import com.company.scopery.modules.documenthub.document.application.response.DocumentDetailResponse;
import com.company.scopery.modules.documenthub.document.application.response.DocumentResponse;
import com.company.scopery.modules.documenthub.document.application.response.DocumentSearchHitResponse;
import com.company.scopery.modules.documenthub.document.application.service.DocumentQueryService;
import com.company.scopery.modules.documenthub.document.http.request.CreateDocumentRequest; import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(DocumentHubApiPaths.DOCUMENTS) @Tag(name="Document Hub - Documents")
public class DocumentController {
    private final CreateDocumentAction create; private final ApproveDocumentAction approve; private final DocumentQueryService query;
    private final ToggleClientVisibilityAction toggleClientVisibility;
    private final ValidateClientVisibilityAction validateClientVisibility;
    public DocumentController(CreateDocumentAction create, ApproveDocumentAction approve, DocumentQueryService query,
                               ToggleClientVisibilityAction toggleClientVisibility,
                               ValidateClientVisibilityAction validateClientVisibility) {
        this.create=create; this.approve=approve; this.query=query;
        this.toggleClientVisibility=toggleClientVisibility;
        this.validateClientVisibility=validateClientVisibility;
    }
    @PostMapping @Operation(summary="Create document")
    public ApiResponse<DocumentResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateDocumentRequest r) {
        return ApiResponse.success(create.execute(new CreateDocumentCommand(projectId, r.folderId(), r.documentTypeCode(), r.code(), r.title(), r.description(), r.contentMode())));
    }
    @GetMapping @Operation(summary="List documents")
    public ApiResponse<List<DocumentResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/search") @Operation(summary="Search documents with Phase 38 masked description snippets")
    public ApiResponse<List<DocumentSearchHitResponse>> search(@PathVariable UUID projectId,
                                                               @RequestParam(required = false) String q) {
        return ApiResponse.success(query.search(projectId, q));
    }
    @GetMapping("/{documentId}") @Operation(summary="Get document")
    public ApiResponse<DocumentResponse> get(@PathVariable UUID projectId, @PathVariable UUID documentId) { return ApiResponse.success(query.get(projectId, documentId)); }
    @GetMapping("/{documentId}/masked") @Operation(summary="Get document with Phase 38 masked sensitive fields")
    public ApiResponse<DocumentDetailResponse> getMasked(@PathVariable UUID projectId, @PathVariable UUID documentId) {
        return ApiResponse.success(query.getWithMaskedFields(projectId, documentId));
    }
    @PostMapping("/{documentId}/approve") @Operation(summary="Approve document")
    public ApiResponse<DocumentResponse> approve(@PathVariable UUID projectId, @PathVariable UUID documentId) {
        return ApiResponse.success(approve.execute(new ApproveDocumentCommand(projectId, documentId)));
    }
    @PostMapping("/{documentId}/client-visibility/enable") @Operation(summary="Enable client visibility for a document")
    public ApiResponse<DocumentResponse> enableClientVisibility(@PathVariable UUID projectId, @PathVariable UUID documentId) {
        return ApiResponse.success(toggleClientVisibility.execute(projectId, documentId, true));
    }
    @PostMapping("/{documentId}/client-visibility/disable") @Operation(summary="Disable client visibility for a document")
    public ApiResponse<DocumentResponse> disableClientVisibility(@PathVariable UUID projectId, @PathVariable UUID documentId) {
        return ApiResponse.success(toggleClientVisibility.execute(projectId, documentId, false));
    }
    @PostMapping("/{documentId}/validate-client-visibility") @Operation(summary="Validate document for client visibility issues")
    public ApiResponse<ValidateClientVisibilityAction.ValidationResult> validateClientVisibility(
            @PathVariable UUID projectId, @PathVariable UUID documentId) {
        return ApiResponse.success(validateClientVisibility.execute(projectId, documentId));
    }
}
