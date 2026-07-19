package com.company.scopery.modules.externalparty.contact.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.externalparty.contact.application.action.CreateExternalContactAction;
import com.company.scopery.modules.externalparty.contact.application.command.CreateExternalContactCommand;
import com.company.scopery.modules.externalparty.contact.application.response.ExternalContactResponse;
import com.company.scopery.modules.externalparty.contact.application.service.ExternalContactQueryService;
import com.company.scopery.modules.externalparty.contact.http.request.CreateExternalContactRequest;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ExternalPartyApiPaths.CONTACTS)
@Tag(name = "External Party - Contacts")
public class ExternalContactController {
    private final CreateExternalContactAction create;
    private final ExternalContactQueryService query;
    public ExternalContactController(CreateExternalContactAction create, ExternalContactQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create contact")
    public ApiResponse<ExternalContactResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateExternalContactRequest r) {
        return ApiResponse.success(create.execute(new CreateExternalContactCommand(workspaceId, r.organizationId(), r.firstName(), r.lastName(), r.email(), r.phone(), r.title(), r.primaryFlag())));
    }
    @GetMapping @Operation(summary = "List contacts")
    public ApiResponse<List<ExternalContactResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @GetMapping("/{contactId}") @Operation(summary = "Get contact")
    public ApiResponse<ExternalContactResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID contactId) {
        return ApiResponse.success(query.get(workspaceId, contactId));
    }
}
