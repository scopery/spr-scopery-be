package com.company.scopery.modules.externalparty.address.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.externalparty.address.application.action.CreateExternalPartyAddressAction;
import com.company.scopery.modules.externalparty.address.application.command.CreateExternalPartyAddressCommand;
import com.company.scopery.modules.externalparty.address.application.response.ExternalPartyAddressResponse;
import com.company.scopery.modules.externalparty.address.application.service.ExternalPartyAddressQueryService;
import com.company.scopery.modules.externalparty.address.http.request.CreateExternalPartyAddressRequest;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ExternalPartyApiPaths.CONTACT_ADDRESSES)
@Tag(name = "External Party - Contact Addresses")
public class ExternalContactAddressController {
    private final CreateExternalPartyAddressAction create;
    private final ExternalPartyAddressQueryService query;

    public ExternalContactAddressController(CreateExternalPartyAddressAction create, ExternalPartyAddressQueryService query) {
        this.create = create; this.query = query;
    }

    @PostMapping @Operation(summary = "Add address to contact")
    public ApiResponse<ExternalPartyAddressResponse> create(@PathVariable UUID workspaceId,
            @PathVariable UUID contactId, @Valid @RequestBody CreateExternalPartyAddressRequest r) {
        return ApiResponse.success(create.execute(new CreateExternalPartyAddressCommand(
                workspaceId, null, contactId, r.addressType(),
                r.line1(), r.line2(), r.city(), r.stateRegion(), r.postalCode(), r.countryCode(), r.primaryFlag())));
    }

    @GetMapping @Operation(summary = "List contact addresses")
    public ApiResponse<List<ExternalPartyAddressResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID contactId) {
        return ApiResponse.success(query.listByContact(workspaceId, contactId));
    }
}
