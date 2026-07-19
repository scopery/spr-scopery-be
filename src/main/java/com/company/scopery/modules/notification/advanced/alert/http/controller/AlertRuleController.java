package com.company.scopery.modules.notification.advanced.alert.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.alert.application.action.CreateAlertRuleAction;
import com.company.scopery.modules.notification.advanced.alert.application.command.CreateAlertRuleCommand;
import com.company.scopery.modules.notification.advanced.alert.application.response.AlertRuleResponse;
import com.company.scopery.modules.notification.advanced.alert.application.service.AlertRuleQueryService;
import com.company.scopery.modules.notification.advanced.alert.http.request.CreateAlertRuleRequest;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(AdvancedNotificationApiPaths.ALERT_RULES) @Tag(name = "Notifications - Alert Rules")
public class AlertRuleController {
    private final CreateAlertRuleAction create; private final AlertRuleQueryService query;
    public AlertRuleController(CreateAlertRuleAction create, AlertRuleQueryService query) { this.create=create; this.query=query; }
    @PostMapping @Operation(summary="Create alert rule")
    public ApiResponse<AlertRuleResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateAlertRuleRequest r) {
        return ApiResponse.success(create.execute(new CreateAlertRuleCommand(workspaceId, r.ruleCode(), r.name(), r.category(), r.conditionJson(), r.bypassQuietHours())));
    }
    @GetMapping @Operation(summary="List alert rules")
    public ApiResponse<List<AlertRuleResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
}
