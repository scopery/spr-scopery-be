package com.company.scopery.modules.notification.emaildelivery.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.emaildelivery.application.EmailDeliveryApplicationService;
import com.company.scopery.modules.notification.emaildelivery.application.query.SearchEmailDeliveriesQuery;
import com.company.scopery.modules.notification.emaildelivery.application.response.EmailDeliveryResponse;
import com.company.scopery.modules.notification.shared.NotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Notification - Email Deliveries", description = "View email delivery records")
@RestController
@RequestMapping(NotificationApiPaths.EMAIL_DELIVERIES)
public class EmailDeliveryController {

    private final EmailDeliveryApplicationService service;

    public EmailDeliveryController(EmailDeliveryApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Get email delivery")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailDeliveryResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getDelivery(id)));
    }

    @Operation(summary = "Search email deliveries")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EmailDeliveryResponse>>> search(
            @RequestParam(required = false) UUID ruleId,
            @RequestParam(required = false) UUID templateId,
            @RequestParam(required = false) UUID eventDefinitionId,
            @RequestParam(required = false) UUID workspaceId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = new SearchEmailDeliveriesQuery(ruleId, templateId, eventDefinitionId, workspaceId, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(service.searchDeliveries(query)));
    }
}
