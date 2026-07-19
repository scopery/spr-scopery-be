package com.company.scopery.modules.iam.auditevent.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.auditevent.application.response.AuditEventResponse;
import com.company.scopery.modules.iam.auditevent.application.service.IamAuditEventQueryService;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "IAM - Audit Events")
@RestController
@RequestMapping(IamApiPaths.IAM_AUDIT_EVENTS)
public class IamAuditEventController {

    private final IamAuditEventQueryService queryService;

    public IamAuditEventController(IamAuditEventQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Search audit events")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditEventResponse>>> search(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID workspaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                queryService.search(eventType, severity, actorId, resourceType,
                        organizationId, workspaceId, page, size)));
    }
}
