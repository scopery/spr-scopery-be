package com.company.scopery.modules.notification.advanced.reminder.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.reminder.application.action.CreateReminderRuleAction;
import com.company.scopery.modules.notification.advanced.reminder.application.command.CreateReminderRuleCommand;
import com.company.scopery.modules.notification.advanced.reminder.application.response.ReminderRuleResponse;
import com.company.scopery.modules.notification.advanced.reminder.application.service.ReminderRuleQueryService;
import com.company.scopery.modules.notification.advanced.reminder.http.request.CreateReminderRuleRequest;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(AdvancedNotificationApiPaths.REMINDER_RULES) @Tag(name = "Notifications - Reminder Rules")
public class ReminderRuleController {
    private final CreateReminderRuleAction create; private final ReminderRuleQueryService query;
    public ReminderRuleController(CreateReminderRuleAction create, ReminderRuleQueryService query) { this.create=create; this.query=query; }
    @PostMapping @Operation(summary="Create reminder rule")
    public ApiResponse<ReminderRuleResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateReminderRuleRequest r) {
        return ApiResponse.success(create.execute(new CreateReminderRuleCommand(workspaceId, r.ruleCode(), r.name(), r.conditionJson(), r.recipientRuleJson())));
    }
    @GetMapping @Operation(summary="List reminder rules")
    public ApiResponse<List<ReminderRuleResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
}
