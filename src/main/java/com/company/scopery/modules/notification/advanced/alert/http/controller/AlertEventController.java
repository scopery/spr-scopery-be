package com.company.scopery.modules.notification.advanced.alert.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.alert.application.action.*;
import com.company.scopery.modules.notification.advanced.alert.application.command.*;
import com.company.scopery.modules.notification.advanced.alert.application.response.AlertEventResponse;
import com.company.scopery.modules.notification.advanced.alert.application.service.AlertEventQueryService;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(AdvancedNotificationApiPaths.ALERT_EVENTS) @Tag(name = "Notifications - Alert Events")
public class AlertEventController {
    private final AlertEventQueryService query; private final AcknowledgeAlertEventAction acknowledge; private final DismissAlertEventAction dismiss;
    public AlertEventController(AlertEventQueryService query, AcknowledgeAlertEventAction acknowledge, DismissAlertEventAction dismiss) {
        this.query=query; this.acknowledge=acknowledge; this.dismiss=dismiss;
    }
    @GetMapping @Operation(summary="List alert events")
    public ApiResponse<List<AlertEventResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @PostMapping("/{alertEventId}/acknowledge") @Operation(summary="Acknowledge an alert event")
    public ApiResponse<AlertEventResponse> acknowledge(@PathVariable UUID workspaceId, @PathVariable UUID alertEventId) {
        return ApiResponse.success(acknowledge.execute(new AcknowledgeAlertEventCommand(workspaceId, alertEventId)));
    }
    @PostMapping("/{alertEventId}/dismiss") @Operation(summary="Dismiss an alert event")
    public ApiResponse<AlertEventResponse> dismiss(@PathVariable UUID workspaceId, @PathVariable UUID alertEventId) {
        return ApiResponse.success(dismiss.execute(new DismissAlertEventCommand(workspaceId, alertEventId)));
    }
}
