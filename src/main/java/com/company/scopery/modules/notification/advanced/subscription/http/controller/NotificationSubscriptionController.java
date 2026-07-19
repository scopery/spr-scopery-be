package com.company.scopery.modules.notification.advanced.subscription.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationApiPaths;
import com.company.scopery.modules.notification.advanced.subscription.application.action.*;
import com.company.scopery.modules.notification.advanced.subscription.application.command.*;
import com.company.scopery.modules.notification.advanced.subscription.application.response.NotificationSubscriptionResponse;
import com.company.scopery.modules.notification.advanced.subscription.application.service.NotificationSubscriptionQueryService;
import com.company.scopery.modules.notification.advanced.subscription.http.request.CreateSubscriptionRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(AdvancedNotificationApiPaths.SUBSCRIPTIONS) @Tag(name = "Notifications - Subscriptions")
public class NotificationSubscriptionController {
    private final CreateSubscriptionAction create; private final UnsubscribeAction unsubscribe; private final NotificationSubscriptionQueryService query;
    public NotificationSubscriptionController(CreateSubscriptionAction create, UnsubscribeAction unsubscribe, NotificationSubscriptionQueryService query) {
        this.create=create; this.unsubscribe=unsubscribe; this.query=query;
    }
    @PostMapping @Operation(summary="Subscribe to target")
    public ApiResponse<NotificationSubscriptionResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateSubscriptionRequest r) {
        return ApiResponse.success(create.execute(new CreateSubscriptionCommand(workspaceId, r.targetType(), r.targetId(), r.subscriptionLevel())));
    }
    @GetMapping @Operation(summary="List my subscriptions")
    public ApiResponse<List<NotificationSubscriptionResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.listMine(workspaceId)); }
    @DeleteMapping("/{subscriptionId}") @Operation(summary="Unsubscribe")
    public ApiResponse<NotificationSubscriptionResponse> unsubscribe(@PathVariable UUID workspaceId, @PathVariable UUID subscriptionId) {
        return ApiResponse.success(unsubscribe.execute(new UnsubscribeCommand(workspaceId, subscriptionId)));
    }
}
