package com.company.scopery.modules.configuration.fieldoption.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.fieldoption.application.action.*;
import com.company.scopery.modules.configuration.fieldoption.application.command.*;
import com.company.scopery.modules.configuration.fieldoption.application.response.CustomFieldOptionResponse;
import com.company.scopery.modules.configuration.fieldoption.application.service.FieldOptionQueryService;
import com.company.scopery.modules.configuration.fieldoption.http.request.CreateFieldOptionRequest;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.CUSTOM_FIELDS + "/{fieldId}/options") @Tag(name = "Configuration - Field Options")
public class FieldOptionController {
    private final CreateFieldOptionAction create; private final ArchiveFieldOptionAction archive; private final FieldOptionQueryService query;
    public FieldOptionController(CreateFieldOptionAction create, ArchiveFieldOptionAction archive, FieldOptionQueryService query) {
        this.create=create; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary="Create field option")
    public ApiResponse<CustomFieldOptionResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID fieldId, @Valid @RequestBody CreateFieldOptionRequest r) {
        return ApiResponse.success(create.execute(new CreateFieldOptionCommand(workspaceId, fieldId, r.optionCode(), r.label(), r.sortOrder())));
    }
    @GetMapping @Operation(summary="List field options")
    public ApiResponse<List<CustomFieldOptionResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID fieldId) {
        return ApiResponse.success(query.list(workspaceId, fieldId));
    }
    @PatchMapping("/{optionId}/archive") @Operation(summary="Archive field option")
    public ApiResponse<CustomFieldOptionResponse> archive(@PathVariable UUID workspaceId, @PathVariable UUID fieldId, @PathVariable UUID optionId) {
        return ApiResponse.success(archive.execute(new ArchiveFieldOptionCommand(workspaceId, fieldId, optionId)));
    }
}
