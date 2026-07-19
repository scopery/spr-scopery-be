package com.company.scopery.modules.trust.sensitiveobject.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.sensitiveobject.application.action.CreateSensitiveObjectRegistryAction;
import com.company.scopery.modules.trust.sensitiveobject.application.action.UpdateSensitiveObjectRegistryAction;
import com.company.scopery.modules.trust.sensitiveobject.application.response.SensitiveObjectRegistryResponse;
import com.company.scopery.modules.trust.sensitiveobject.application.service.SensitiveObjectRegistryQueryService;
import com.company.scopery.modules.trust.sensitiveobject.http.request.CreateSensitiveObjectRegistryRequest;
import com.company.scopery.modules.trust.sensitiveobject.http.request.UpdateSensitiveObjectRegistryRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class SensitiveObjectRegistryController {
    private final CreateSensitiveObjectRegistryAction createAction;
    private final UpdateSensitiveObjectRegistryAction updateAction;
    private final SensitiveObjectRegistryQueryService queryService;
    public SensitiveObjectRegistryController(CreateSensitiveObjectRegistryAction createAction,
            UpdateSensitiveObjectRegistryAction updateAction, SensitiveObjectRegistryQueryService queryService) {
        this.createAction = createAction; this.updateAction = updateAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.SENSITIVE_OBJECTS) @Operation(summary = "Register sensitive object type")
    public ApiResponse<SensitiveObjectRegistryResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateSensitiveObjectRegistryRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r.objectTypeCode(), r.classification()));
    }
    @GetMapping(TrustApiPaths.SENSITIVE_OBJECTS) @Operation(summary = "List sensitive object registry entries")
    public ApiResponse<List<SensitiveObjectRegistryResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.list(workspaceId));
    }
    @GetMapping(TrustApiPaths.SENSITIVE_OBJECT_BY_ID) @Operation(summary = "Get sensitive object registry entry")
    public ApiResponse<SensitiveObjectRegistryResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID objectId) {
        return ApiResponse.success(queryService.get(workspaceId, objectId));
    }
    @PatchMapping(TrustApiPaths.SENSITIVE_OBJECT_BY_ID) @Operation(summary = "Update or deactivate sensitive object registry entry")
    public ApiResponse<SensitiveObjectRegistryResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID objectId,
            @RequestBody UpdateSensitiveObjectRegistryRequest r) {
        return ApiResponse.success(updateAction.execute(workspaceId, objectId, r.classification(), r.exportReasonRequired(), r.searchIndexAllowed(), r.deactivate()));
    }
}
