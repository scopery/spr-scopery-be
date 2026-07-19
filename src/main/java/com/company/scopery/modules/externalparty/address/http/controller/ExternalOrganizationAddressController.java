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
@RequestMapping(ExternalPartyApiPaths.ORGANIZATION_ADDRESSES)
@Tag(name = "External Party - Organization Addresses")
public class ExternalOrganizationAddressController {
    private final CreateExternalPartyAddressAction create;
    private final ExternalPartyAddressQueryService query;

    public ExternalOrganizationAddressController(CreateExternalPartyAddressAction create, ExternalPartyAddressQueryService query) {
        this.create = create; this.query = query;
    }

    @PostMapping @Operation(summary = "Add address to organization")
    public ApiResponse<ExternalPartyAddressResponse> create(@PathVariable UUID workspaceId,
            @PathVariable UUID organizationId, @Valid @RequestBody CreateExternalPartyAddressRequest r) {
        return ApiResponse.success(create.execute(new CreateExternalPartyAddressCommand(
                workspaceId, organizationId, null, r.addressType(),
                r.line1(), r.line2(), r.city(), r.stateRegion(), r.postalCode(), r.countryCode(), r.primaryFlag())));
    }

    @GetMapping @Operation(summary = "List organization addresses")
    public ApiResponse<List<ExternalPartyAddressResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID organizationId) {
        return ApiResponse.success(query.listByOrganization(workspaceId, organizationId));
    }
}
