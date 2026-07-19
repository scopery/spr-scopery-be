package com.company.scopery.modules.notification.advanced.reminder.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.reminder.application.action.*;
import com.company.scopery.modules.notification.advanced.reminder.application.command.*;
import com.company.scopery.modules.notification.advanced.reminder.application.response.ReminderInstanceResponse;
import com.company.scopery.modules.notification.advanced.reminder.application.service.ReminderInstanceQueryService;
import com.company.scopery.modules.notification.advanced.reminder.http.request.SnoozeReminderRequest;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(AdvancedNotificationApiPaths.REMINDER_INSTANCES) @Tag(name = "Notifications - Reminder Instances")
public class ReminderInstanceController {
    private final ReminderInstanceQueryService query; private final SnoozeReminderInstanceAction snooze; private final DismissReminderInstanceAction dismiss;
    public ReminderInstanceController(ReminderInstanceQueryService query, SnoozeReminderInstanceAction snooze, DismissReminderInstanceAction dismiss) {
        this.query=query; this.snooze=snooze; this.dismiss=dismiss;
    }
    @GetMapping @Operation(summary="List my reminder instances")
    public ApiResponse<List<ReminderInstanceResponse>> listMine(@PathVariable UUID workspaceId) { return ApiResponse.success(query.listMine(workspaceId)); }
    @PostMapping("/{reminderInstanceId}/snooze") @Operation(summary="Snooze a reminder")
    public ApiResponse<ReminderInstanceResponse> snooze(@PathVariable UUID workspaceId, @PathVariable UUID reminderInstanceId, @Valid @RequestBody SnoozeReminderRequest r) {
        return ApiResponse.success(snooze.execute(new SnoozeReminderInstanceCommand(workspaceId, reminderInstanceId, r.snoozedUntil())));
    }
    @PostMapping("/{reminderInstanceId}/dismiss") @Operation(summary="Dismiss a reminder")
    public ApiResponse<ReminderInstanceResponse> dismiss(@PathVariable UUID workspaceId, @PathVariable UUID reminderInstanceId) {
        return ApiResponse.success(dismiss.execute(new DismissReminderInstanceCommand(workspaceId, reminderInstanceId)));
    }
}
