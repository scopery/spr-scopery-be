package com.company.scopery.modules.externalparty.channel.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.externalparty.channel.application.action.CreateExternalContactChannelAction;
import com.company.scopery.modules.externalparty.channel.application.command.CreateExternalContactChannelCommand;
import com.company.scopery.modules.externalparty.channel.application.response.ExternalContactChannelResponse;
import com.company.scopery.modules.externalparty.channel.application.service.ExternalContactChannelQueryService;
import com.company.scopery.modules.externalparty.channel.http.request.CreateExternalContactChannelRequest;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ExternalPartyApiPaths.CONTACT_CHANNELS)
@Tag(name = "External Party - Contact Channels")
public class ExternalContactChannelController {
    private final CreateExternalContactChannelAction create;
    private final ExternalContactChannelQueryService query;

    public ExternalContactChannelController(CreateExternalContactChannelAction create, ExternalContactChannelQueryService query) {
        this.create = create; this.query = query;
    }

    @PostMapping @Operation(summary = "Add channel to contact")
    public ApiResponse<ExternalContactChannelResponse> create(@PathVariable UUID workspaceId,
            @PathVariable UUID contactId, @Valid @RequestBody CreateExternalContactChannelRequest r) {
        return ApiResponse.success(create.execute(new CreateExternalContactChannelCommand(
                workspaceId, contactId, r.channelType(), r.channelValue(), r.primaryFlag())));
    }

    @GetMapping @Operation(summary = "List contact channels")
    public ApiResponse<List<ExternalContactChannelResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID contactId) {
        return ApiResponse.success(query.listByContact(workspaceId, contactId));
    }
}
