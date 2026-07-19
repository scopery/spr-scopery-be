package com.company.scopery.modules.notification.advanced.digest.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.digest.application.action.CreateDigestRuleAction;
import com.company.scopery.modules.notification.advanced.digest.application.command.CreateDigestRuleCommand;
import com.company.scopery.modules.notification.advanced.digest.application.response.DigestRuleResponse;
import com.company.scopery.modules.notification.advanced.digest.application.service.DigestRuleQueryService;
import com.company.scopery.modules.notification.advanced.digest.http.request.CreateDigestRuleRequest;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(AdvancedNotificationApiPaths.DIGEST_RULES) @Tag(name = "Notifications - Digest Rules")
public class DigestRuleController {
    private final CreateDigestRuleAction create; private final DigestRuleQueryService query;
    public DigestRuleController(CreateDigestRuleAction create, DigestRuleQueryService query) { this.create=create; this.query=query; }
    @PostMapping @Operation(summary="Create digest rule")
    public ApiResponse<DigestRuleResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateDigestRuleRequest r) {
        return ApiResponse.success(create.execute(new CreateDigestRuleCommand(workspaceId, r.code(), r.name(), r.scope(), r.frequency(), r.scheduleConfigJson())));
    }
    @GetMapping @Operation(summary="List digest rules")
    public ApiResponse<List<DigestRuleResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
}
