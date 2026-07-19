package com.company.scopery.modules.trust.consent.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.consent.application.action.CreateContactSuppressionAction;
import com.company.scopery.modules.trust.consent.application.action.ReleaseContactSuppressionAction;
import com.company.scopery.modules.trust.consent.application.response.ContactSuppressionResponse;
import com.company.scopery.modules.trust.consent.application.service.ContactSuppressionQueryService;
import com.company.scopery.modules.trust.consent.http.request.CreateContactSuppressionRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class ContactSuppressionController {
    private final CreateContactSuppressionAction createAction;
    private final ReleaseContactSuppressionAction releaseAction;
    private final ContactSuppressionQueryService queryService;
    public ContactSuppressionController(CreateContactSuppressionAction createAction, ReleaseContactSuppressionAction releaseAction, ContactSuppressionQueryService queryService) {
        this.createAction = createAction; this.releaseAction = releaseAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.CONTACT_SUPPRESSIONS) @Operation(summary = "Create contact suppression")
    public ApiResponse<ContactSuppressionResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateContactSuppressionRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r.externalContactId(), r.portalAccountId(), r.suppressionType(), r.reason()));
    }
    @GetMapping(TrustApiPaths.CONTACT_SUPPRESSIONS) @Operation(summary = "List contact suppressions")
    public ApiResponse<List<ContactSuppressionResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
    @PostMapping(TrustApiPaths.CONTACT_SUPPRESSIONS + "/{suppressionId}/release") @Operation(summary = "Release contact suppression")
    public ApiResponse<ContactSuppressionResponse> release(@PathVariable UUID workspaceId, @PathVariable UUID suppressionId,
            @RequestParam(required = false) String releaseReason) {
        return ApiResponse.success(releaseAction.execute(workspaceId, suppressionId, releaseReason));
    }
}
