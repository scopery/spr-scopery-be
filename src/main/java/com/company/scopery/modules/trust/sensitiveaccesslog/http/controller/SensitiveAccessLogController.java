package com.company.scopery.modules.trust.sensitiveaccesslog.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.sensitiveaccesslog.application.action.RecordSensitiveAccessAction;
import com.company.scopery.modules.trust.sensitiveaccesslog.application.response.SensitiveAccessLogResponse;
import com.company.scopery.modules.trust.sensitiveaccesslog.application.service.SensitiveAccessLogQueryService;
import com.company.scopery.modules.trust.sensitiveaccesslog.http.request.RecordSensitiveAccessRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class SensitiveAccessLogController {
    private final RecordSensitiveAccessAction recordAction;
    private final SensitiveAccessLogQueryService queryService;
    public SensitiveAccessLogController(RecordSensitiveAccessAction recordAction, SensitiveAccessLogQueryService queryService) {
        this.recordAction = recordAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.SENSITIVE_ACCESS_LOGS) @Operation(summary = "Record sensitive field access log")
    public ApiResponse<SensitiveAccessLogResponse> record(@PathVariable UUID workspaceId,
            @Valid @RequestBody RecordSensitiveAccessRequest r) {
        UUID targetObjectId = r.targetObjectId() != null ? UUID.fromString(r.targetObjectId()) : null;
        return ApiResponse.success(recordAction.execute(workspaceId, null, "USER", null,
                r.targetObjectType(), targetObjectId, r.fieldPath(), r.classification(),
                r.accessAction(), null, r.reason(), null, null));
    }
    @GetMapping(TrustApiPaths.SENSITIVE_ACCESS_LOGS) @Operation(summary = "List sensitive access logs")
    public ApiResponse<List<SensitiveAccessLogResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
}
