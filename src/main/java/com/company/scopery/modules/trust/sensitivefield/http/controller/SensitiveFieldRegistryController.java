package com.company.scopery.modules.trust.sensitivefield.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.sensitivefield.application.action.CreateSensitiveFieldRegistryAction;
import com.company.scopery.modules.trust.sensitivefield.application.action.UpdateSensitiveFieldRegistryAction;
import com.company.scopery.modules.trust.sensitivefield.application.response.SensitiveFieldRegistryResponse;
import com.company.scopery.modules.trust.sensitivefield.application.service.SensitiveFieldRegistryQueryService;
import com.company.scopery.modules.trust.sensitivefield.http.request.CreateSensitiveFieldRegistryRequest;
import com.company.scopery.modules.trust.sensitivefield.http.request.UpdateSensitiveFieldRegistryRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class SensitiveFieldRegistryController {
    private final CreateSensitiveFieldRegistryAction createAction;
    private final UpdateSensitiveFieldRegistryAction updateAction;
    private final SensitiveFieldRegistryQueryService queryService;
    public SensitiveFieldRegistryController(CreateSensitiveFieldRegistryAction createAction,
            UpdateSensitiveFieldRegistryAction updateAction, SensitiveFieldRegistryQueryService queryService) {
        this.createAction = createAction; this.updateAction = updateAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.SENSITIVE_FIELDS) @Operation(summary = "Register sensitive field")
    public ApiResponse<SensitiveFieldRegistryResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateSensitiveFieldRegistryRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r.objectTypeCode(), r.fieldPath(), r.classification(), r.maskingStrategy()));
    }
    @GetMapping(TrustApiPaths.SENSITIVE_FIELDS) @Operation(summary = "List sensitive field registry entries")
    public ApiResponse<List<SensitiveFieldRegistryResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.list(workspaceId));
    }
    @GetMapping(TrustApiPaths.SENSITIVE_FIELD_BY_ID) @Operation(summary = "Get sensitive field registry entry")
    public ApiResponse<SensitiveFieldRegistryResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID fieldId) {
        return ApiResponse.success(queryService.get(workspaceId, fieldId));
    }
    @PatchMapping(TrustApiPaths.SENSITIVE_FIELD_BY_ID) @Operation(summary = "Update or deactivate sensitive field registry entry")
    public ApiResponse<SensitiveFieldRegistryResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID fieldId,
            @RequestBody UpdateSensitiveFieldRegistryRequest r) {
        return ApiResponse.success(updateAction.execute(workspaceId, fieldId, r.classification(), r.maskingStrategy(), r.exportAllowed(), r.deactivate()));
    }
}
