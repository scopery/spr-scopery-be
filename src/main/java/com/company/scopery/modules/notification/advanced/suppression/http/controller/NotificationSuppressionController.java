package com.company.scopery.modules.notification.advanced.suppression.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import com.company.scopery.modules.notification.advanced.suppression.application.response.NotificationSuppressionResponse;
import com.company.scopery.modules.notification.advanced.suppression.application.service.NotificationSuppressionQueryService;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(AdvancedNotificationApiPaths.SUPPRESSIONS) @Tag(name = "Notifications - Suppressions")
public class NotificationSuppressionController {
    private final NotificationSuppressionQueryService query;
    public NotificationSuppressionController(NotificationSuppressionQueryService query) { this.query=query; }
    @GetMapping @Operation(summary="List notification suppressions for workspace")
    public ApiResponse<List<NotificationSuppressionResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.list(workspaceId));
    }
}
