package com.company.scopery.modules.documenthub.share.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.share.application.action.*;
import com.company.scopery.modules.documenthub.share.application.command.CreateDocumentShareCommand;
import com.company.scopery.modules.documenthub.share.application.command.RevokeDocumentShareCommand;
import com.company.scopery.modules.documenthub.share.application.response.DocumentShareResponse;
import com.company.scopery.modules.documenthub.share.application.service.DocumentShareQueryService;
import com.company.scopery.modules.documenthub.share.http.request.CreateDocumentShareRequest;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(DocumentHubApiPaths.DOCUMENT_SHARES)
@Tag(name = "Document Hub - Shares")
public class DocumentShareController {
    private final CreateDocumentShareAction create;
    private final RevokeDocumentShareAction revoke;
    private final DocumentShareQueryService query;
    public DocumentShareController(CreateDocumentShareAction create, RevokeDocumentShareAction revoke, DocumentShareQueryService query) {
        this.create=create; this.revoke=revoke; this.query=query;
    }
    @PostMapping @Operation(summary = "Create share token/grant")
    public ApiResponse<DocumentShareResponse> create(@PathVariable UUID projectId, @PathVariable UUID documentId, @Valid @RequestBody CreateDocumentShareRequest r) {
        return ApiResponse.success(create.execute(new CreateDocumentShareCommand(projectId, documentId, r.shareType(), r.granteeType(), r.granteeId(), r.expiresAt())));
    }
    @GetMapping @Operation(summary = "List shares")
    public ApiResponse<List<DocumentShareResponse>> list(@PathVariable UUID projectId, @PathVariable UUID documentId) {
        return ApiResponse.success(query.list(projectId, documentId));
    }
    @PostMapping("/{shareId}/revoke") @Operation(summary = "Revoke share")
    public ApiResponse<DocumentShareResponse> revoke(@PathVariable UUID projectId, @PathVariable UUID shareId) {
        return ApiResponse.success(revoke.execute(new RevokeDocumentShareCommand(projectId, shareId)));
    }
}
