package com.company.scopery.modules.externalparty.preference.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.externalparty.preference.application.action.UpsertCommunicationPreferenceAction;
import com.company.scopery.modules.externalparty.preference.application.command.UpsertCommunicationPreferenceCommand;
import com.company.scopery.modules.externalparty.preference.application.response.CommunicationPreferenceResponse;
import com.company.scopery.modules.externalparty.preference.application.service.CommunicationPreferenceQueryService;
import com.company.scopery.modules.externalparty.preference.http.request.UpsertCommunicationPreferenceRequest;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping(ExternalPartyApiPaths.ORGANIZATION_PREFERENCE)
@Tag(name = "External Party - Communication Preference")
public class ExternalOrganizationPreferenceController {
    private final UpsertCommunicationPreferenceAction upsert;
    private final CommunicationPreferenceQueryService query;

    public ExternalOrganizationPreferenceController(UpsertCommunicationPreferenceAction upsert, CommunicationPreferenceQueryService query) {
        this.upsert = upsert; this.query = query;
    }

    @GetMapping @Operation(summary = "Get organization communication preference")
    public ApiResponse<CommunicationPreferenceResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID organizationId) {
        return ApiResponse.success(query.getByOrganization(workspaceId, organizationId));
    }

    @PutMapping @Operation(summary = "Upsert organization communication preference")
    public ApiResponse<CommunicationPreferenceResponse> upsert(@PathVariable UUID workspaceId,
            @PathVariable UUID organizationId, @RequestBody UpsertCommunicationPreferenceRequest r) {
        return ApiResponse.success(upsert.execute(new UpsertCommunicationPreferenceCommand(
                workspaceId, organizationId, null, r.preferredChannelType(), r.preferredLanguage(), r.doNotContact(), r.notes())));
    }
}
