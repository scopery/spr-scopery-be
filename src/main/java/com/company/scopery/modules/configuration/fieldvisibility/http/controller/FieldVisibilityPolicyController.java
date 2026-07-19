package com.company.scopery.modules.configuration.fieldvisibility.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.fieldvisibility.application.action.SetFieldVisibilityPolicyAction;
import com.company.scopery.modules.configuration.fieldvisibility.application.command.SetFieldVisibilityPolicyCommand;
import com.company.scopery.modules.configuration.fieldvisibility.application.response.FieldVisibilityPolicyResponse;
import com.company.scopery.modules.configuration.fieldvisibility.application.service.FieldVisibilityPolicyQueryService;
import com.company.scopery.modules.configuration.fieldvisibility.http.request.SetFieldVisibilityPolicyRequest;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.FIELD_VISIBILITY_POLICIES) @Tag(name = "Configuration - Field Visibility")
public class FieldVisibilityPolicyController {
    private final SetFieldVisibilityPolicyAction set; private final FieldVisibilityPolicyQueryService query;
    public FieldVisibilityPolicyController(SetFieldVisibilityPolicyAction set, FieldVisibilityPolicyQueryService query) { this.set=set; this.query=query; }
    @PutMapping @Operation(summary="Set field visibility policy")
    public ApiResponse<FieldVisibilityPolicyResponse> set(@PathVariable UUID workspaceId, @PathVariable UUID fieldId, @Valid @RequestBody SetFieldVisibilityPolicyRequest r) {
        return ApiResponse.success(set.execute(new SetFieldVisibilityPolicyCommand(workspaceId, fieldId, r.audienceType(), r.visible())));
    }
    @GetMapping @Operation(summary="List field visibility policies")
    public ApiResponse<List<FieldVisibilityPolicyResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID fieldId) {
        return ApiResponse.success(query.listByField(workspaceId, fieldId));
    }
}
