package com.company.scopery.modules.trust.classification.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.classification.application.action.UpsertDataClassificationPolicyAction;
import com.company.scopery.modules.trust.classification.application.response.DataClassificationPolicyResponse;
import com.company.scopery.modules.trust.classification.application.service.DataClassificationPolicyQueryService;
import com.company.scopery.modules.trust.classification.http.request.UpsertDataClassificationPolicyRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class DataClassificationPolicyController {
    private final UpsertDataClassificationPolicyAction upsertAction;
    private final DataClassificationPolicyQueryService queryService;
    public DataClassificationPolicyController(UpsertDataClassificationPolicyAction upsertAction, DataClassificationPolicyQueryService queryService) {
        this.upsertAction = upsertAction; this.queryService = queryService;
    }
    @GetMapping(TrustApiPaths.CLASSIFICATION_POLICY) @Operation(summary = "Get data classification policy")
    public ApiResponse<DataClassificationPolicyResponse> get(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.getByWorkspace(workspaceId));
    }
    @PutMapping(TrustApiPaths.CLASSIFICATION_POLICY) @Operation(summary = "Upsert data classification policy")
    public ApiResponse<DataClassificationPolicyResponse> upsert(@PathVariable UUID workspaceId,
            @Valid @RequestBody UpsertDataClassificationPolicyRequest r) {
        return ApiResponse.success(upsertAction.execute(workspaceId, r.policyCode(), r.name(), r.defaultClassification(), r.description()));
    }
}
