package com.company.scopery.modules.notification.advanced.digest.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.digest.application.response.DigestRunResponse;
import com.company.scopery.modules.notification.advanced.digest.application.service.DigestRunQueryService;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(AdvancedNotificationApiPaths.DIGEST_RUNS) @Tag(name = "Notifications - Digest Runs")
public class DigestRunController {
    private final DigestRunQueryService query;
    public DigestRunController(DigestRunQueryService query) { this.query=query; }
    @GetMapping @Operation(summary="List all digest runs for workspace")
    public ApiResponse<List<DigestRunResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @GetMapping("/me") @Operation(summary="List my digest runs")
    public ApiResponse<List<DigestRunResponse>> listMine(@PathVariable UUID workspaceId) { return ApiResponse.success(query.listMine(workspaceId)); }
}
