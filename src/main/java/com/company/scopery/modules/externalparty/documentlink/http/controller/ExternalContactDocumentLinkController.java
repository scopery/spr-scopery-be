package com.company.scopery.modules.externalparty.documentlink.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.externalparty.documentlink.application.action.CreateExternalPartyDocumentLinkAction;
import com.company.scopery.modules.externalparty.documentlink.application.command.CreateExternalPartyDocumentLinkCommand;
import com.company.scopery.modules.externalparty.documentlink.application.response.ExternalPartyDocumentLinkResponse;
import com.company.scopery.modules.externalparty.documentlink.application.service.ExternalPartyDocumentLinkQueryService;
import com.company.scopery.modules.externalparty.documentlink.http.request.CreateExternalPartyDocumentLinkRequest;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ExternalPartyApiPaths.CONTACT_DOC_LINKS)
@Tag(name = "External Party - Document Links")
public class ExternalContactDocumentLinkController {
    private final CreateExternalPartyDocumentLinkAction create;
    private final ExternalPartyDocumentLinkQueryService query;

    public ExternalContactDocumentLinkController(CreateExternalPartyDocumentLinkAction create, ExternalPartyDocumentLinkQueryService query) {
        this.create = create; this.query = query;
    }

    @PostMapping @Operation(summary = "Link document to contact")
    public ApiResponse<ExternalPartyDocumentLinkResponse> create(@PathVariable UUID workspaceId,
            @PathVariable UUID contactId, @Valid @RequestBody CreateExternalPartyDocumentLinkRequest r) {
        return ApiResponse.success(create.execute(new CreateExternalPartyDocumentLinkCommand(
                workspaceId, null, contactId, r.documentId(), r.linkNote())));
    }

    @GetMapping @Operation(summary = "List contact document links")
    public ApiResponse<List<ExternalPartyDocumentLinkResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID contactId) {
        return ApiResponse.success(query.listByContact(workspaceId, contactId));
    }
}
