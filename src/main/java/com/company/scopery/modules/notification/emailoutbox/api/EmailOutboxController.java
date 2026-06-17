package com.company.scopery.modules.notification.emailoutbox.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.emailoutbox.application.EmailOutboxApplicationService;
import com.company.scopery.modules.notification.emailoutbox.application.query.SearchEmailOutboxQuery;
import com.company.scopery.modules.notification.emailoutbox.application.response.EmailOutboxResponse;
import com.company.scopery.modules.notification.shared.NotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Notification - Email Outbox", description = "View and retry email outbox records")
@RestController
@RequestMapping(NotificationApiPaths.EMAIL_OUTBOX)
public class EmailOutboxController {

    private final EmailOutboxApplicationService service;

    public EmailOutboxController(EmailOutboxApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Get email outbox record")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailOutboxResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getOutbox(id)));
    }

    @Operation(summary = "Search email outbox")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EmailOutboxResponse>>> search(
            @RequestParam(required = false) UUID deliveryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String providerType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = new SearchEmailOutboxQuery(deliveryId, status, providerType, page, size);
        return ResponseEntity.ok(ApiResponse.success(service.searchOutbox(query)));
    }

    @Operation(summary = "Retry failed outbox record")
    @PostMapping("/{id}/retry")
    public ResponseEntity<ApiResponse<EmailOutboxResponse>> retry(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.retryOutbox(id)));
    }
}
