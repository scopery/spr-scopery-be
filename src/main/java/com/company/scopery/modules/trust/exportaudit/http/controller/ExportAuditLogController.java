package com.company.scopery.modules.trust.exportaudit.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.exportaudit.application.action.CreateExportAuditLogAction;
import com.company.scopery.modules.trust.exportaudit.application.response.ExportAuditLogResponse;
import com.company.scopery.modules.trust.exportaudit.application.service.ExportAuditLogQueryService;
import com.company.scopery.modules.trust.exportaudit.http.request.CreateExportAuditLogRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class ExportAuditLogController {
    private final CreateExportAuditLogAction createAction;
    private final ExportAuditLogQueryService queryService;
    public ExportAuditLogController(CreateExportAuditLogAction createAction, ExportAuditLogQueryService queryService) {
        this.createAction = createAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.EXPORT_AUDIT_LOGS) @Operation(summary = "Record export audit log")
    public ApiResponse<ExportAuditLogResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateExportAuditLogRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r.exportType(), r.targetObjectType(), r.classification(), r.reason()));
    }
    @GetMapping(TrustApiPaths.EXPORT_AUDIT_LOGS) @Operation(summary = "List export audit logs")
    public ApiResponse<List<ExportAuditLogResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
}
