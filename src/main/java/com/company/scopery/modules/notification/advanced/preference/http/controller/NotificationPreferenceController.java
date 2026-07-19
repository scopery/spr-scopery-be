package com.company.scopery.modules.notification.advanced.preference.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.preference.application.action.UpsertMyPreferenceAction;
import com.company.scopery.modules.notification.advanced.preference.application.command.UpsertMyPreferenceCommand;
import com.company.scopery.modules.notification.advanced.preference.application.response.NotificationPreferenceProfileResponse;
import com.company.scopery.modules.notification.advanced.preference.application.service.NotificationPreferenceQueryService;
import com.company.scopery.modules.notification.advanced.preference.http.request.UpdatePreferenceRequest;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping(AdvancedNotificationApiPaths.PREFERENCES_ME) @Tag(name = "Notifications - Preferences")
public class NotificationPreferenceController {
    private final NotificationPreferenceQueryService query; private final UpsertMyPreferenceAction upsert;
    public NotificationPreferenceController(NotificationPreferenceQueryService query, UpsertMyPreferenceAction upsert) { this.query=query; this.upsert=upsert; }
    @GetMapping @Operation(summary="Get my notification preferences")
    public ApiResponse<NotificationPreferenceProfileResponse> me(@PathVariable UUID workspaceId) { return ApiResponse.success(query.me(workspaceId)); }
    @PutMapping @Operation(summary="Update my notification preferences")
    public ApiResponse<NotificationPreferenceProfileResponse> update(@PathVariable UUID workspaceId, @RequestBody UpdatePreferenceRequest r) {
        return ApiResponse.success(upsert.execute(new UpsertMyPreferenceCommand(workspaceId, r.timezone(), r.defaultMode(), r.digestEnabled(), r.quietHoursEnabled(), r.quietHoursStart(), r.quietHoursEnd())));
    }
}
