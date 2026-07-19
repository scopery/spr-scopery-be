package com.company.scopery.modules.clientportal.auditlog.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.auditlog.application.response.ExternalPortalAuditLogResponse;
import com.company.scopery.modules.clientportal.auditlog.application.service.ExternalPortalAuditLogQueryService;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.PORTAL_AUDIT_LOGS)
@Tag(name = "Client Portal - Audit Logs")
public class ExternalPortalAuditLogController {
    private final ExternalPortalAuditLogQueryService query;
    public ExternalPortalAuditLogController(ExternalPortalAuditLogQueryService query) { this.query = query; }
    @GetMapping @Operation(summary = "List portal audit logs for project")
    public ApiResponse<List<ExternalPortalAuditLogResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listByProject(projectId));
    }
}
