package com.company.scopery.modules.notification.notificationitem.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.notificationitem.application.action.DismissNotificationAction;
import com.company.scopery.modules.notification.notificationitem.application.action.MarkAllNotificationsReadAction;
import com.company.scopery.modules.notification.notificationitem.application.action.MarkNotificationReadAction;
import com.company.scopery.modules.notification.notificationitem.application.response.NotificationItemResponse;
import com.company.scopery.modules.notification.notificationitem.application.response.UnreadCountResponse;
import com.company.scopery.modules.notification.notificationitem.application.service.NotificationItemQueryService;
import com.company.scopery.modules.notification.shared.NotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Notifications", description = "In-app notification inbox for the current user")
@RestController
@RequestMapping(NotificationApiPaths.NOTIFICATIONS)
public class NotificationItemController {

    private final NotificationItemQueryService queryService;
    private final MarkNotificationReadAction markReadAction;
    private final MarkAllNotificationsReadAction markAllReadAction;
    private final DismissNotificationAction dismissAction;

    public NotificationItemController(NotificationItemQueryService queryService,
                                       MarkNotificationReadAction markReadAction,
                                       MarkAllNotificationsReadAction markAllReadAction,
                                       DismissNotificationAction dismissAction) {
        this.queryService = queryService;
        this.markReadAction = markReadAction;
        this.markAllReadAction = markAllReadAction;
        this.dismissAction = dismissAction;
    }

    @Operation(summary = "List my notifications")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationItemResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean includeDismissed) {
        return ResponseEntity.ok(ApiResponse.success(queryService.listMine(page, size, includeDismissed)));
    }

    @Operation(summary = "Unread notification count")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> unreadCount() {
        return ResponseEntity.ok(ApiResponse.success(queryService.unreadCount()));
    }

    @Operation(summary = "Mark notification as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationItemResponse>> markRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(markReadAction.execute(id)));
    }

    @Operation(summary = "Mark all notifications as read")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> markAllRead() {
        return ResponseEntity.ok(ApiResponse.success(markAllReadAction.execute()));
    }

    @Operation(summary = "Dismiss notification")
    @PatchMapping("/{id}/dismiss")
    public ResponseEntity<ApiResponse<NotificationItemResponse>> dismiss(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(dismissAction.execute(id)));
    }
}
