package com.company.scopery.modules.notification.advanced.channelpref.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.channelpref.application.action.UpsertChannelPreferenceAction;
import com.company.scopery.modules.notification.advanced.channelpref.application.command.UpsertChannelPreferenceCommand;
import com.company.scopery.modules.notification.advanced.channelpref.application.response.NotificationChannelPreferenceResponse;
import com.company.scopery.modules.notification.advanced.channelpref.application.service.ChannelPreferenceQueryService;
import com.company.scopery.modules.notification.advanced.channelpref.http.request.UpsertChannelPreferenceRequest;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@RestController
@RequestMapping(AdvancedNotificationApiPaths.CHANNEL_PREFERENCES)
@Tag(name = "Notifications - Channel Preferences")
public class ChannelPreferenceController {
    private final ChannelPreferenceQueryService query;
    private final UpsertChannelPreferenceAction upsert;

    public ChannelPreferenceController(ChannelPreferenceQueryService query, UpsertChannelPreferenceAction upsert) {
        this.query = query; this.upsert = upsert;
    }

    @GetMapping @Operation(summary = "List my category/channel notification preferences")
    public ApiResponse<List<NotificationChannelPreferenceResponse>> me(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.me(workspaceId));
    }

    @PutMapping @Operation(summary = "Upsert category/channel preference")
    public ApiResponse<NotificationChannelPreferenceResponse> upsert(@PathVariable UUID workspaceId,
                                                                     @Valid @RequestBody UpsertChannelPreferenceRequest r) {
        return ApiResponse.success(upsert.execute(new UpsertChannelPreferenceCommand(workspaceId, r.categoryCode(), r.channelCode(), r.enabled())));
    }
}
