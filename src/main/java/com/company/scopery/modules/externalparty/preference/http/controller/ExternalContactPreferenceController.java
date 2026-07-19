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
@RequestMapping(ExternalPartyApiPaths.CONTACT_PREFERENCE)
@Tag(name = "External Party - Communication Preference")
public class ExternalContactPreferenceController {
    private final UpsertCommunicationPreferenceAction upsert;
    private final CommunicationPreferenceQueryService query;

    public ExternalContactPreferenceController(UpsertCommunicationPreferenceAction upsert, CommunicationPreferenceQueryService query) {
        this.upsert = upsert; this.query = query;
    }

    @GetMapping @Operation(summary = "Get contact communication preference")
    public ApiResponse<CommunicationPreferenceResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID contactId) {
        return ApiResponse.success(query.getByContact(workspaceId, contactId));
    }

    @PutMapping @Operation(summary = "Upsert contact communication preference")
    public ApiResponse<CommunicationPreferenceResponse> upsert(@PathVariable UUID workspaceId,
            @PathVariable UUID contactId, @RequestBody UpsertCommunicationPreferenceRequest r) {
        return ApiResponse.success(upsert.execute(new UpsertCommunicationPreferenceCommand(
                workspaceId, null, contactId, r.preferredChannelType(), r.preferredLanguage(), r.doNotContact(), r.notes())));
    }
}
