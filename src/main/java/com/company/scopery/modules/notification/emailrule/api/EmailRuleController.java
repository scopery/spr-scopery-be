package com.company.scopery.modules.notification.emailrule.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.emailrule.api.request.CreateEmailRuleRequest;
import com.company.scopery.modules.notification.emailrule.api.request.UpdateEmailRuleRequest;
import com.company.scopery.modules.notification.emailrule.application.EmailRuleApplicationService;
import com.company.scopery.modules.notification.emailrule.application.command.CreateEmailRuleCommand;
import com.company.scopery.modules.notification.emailrule.application.command.UpdateEmailRuleCommand;
import com.company.scopery.modules.notification.emailrule.application.query.SearchEmailRulesQuery;
import com.company.scopery.modules.notification.emailrule.application.response.EmailRuleResponse;
import com.company.scopery.modules.notification.shared.NotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Notification - Email Rules", description = "Manage email notification rules")
@RestController
@RequestMapping(NotificationApiPaths.EMAIL_RULES)
public class EmailRuleController {

    private final EmailRuleApplicationService service;

    public EmailRuleController(EmailRuleApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create email rule")
    @PostMapping
    public ResponseEntity<ApiResponse<EmailRuleResponse>> create(@Valid @RequestBody CreateEmailRuleRequest req) {
        var cmd = new CreateEmailRuleCommand(req.code(), req.name(), req.description(),
                req.scope(), req.workspaceId(), req.eventDefinitionId(), req.templateId(),
                req.recipientStrategy(), req.recipientConfigJson(), req.priority());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.createRule(cmd)));
    }

    @Operation(summary = "Update email rule")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateEmailRuleRequest req) {
        var cmd = new UpdateEmailRuleCommand(id, req.name(), req.description(),
                req.recipientStrategy(), req.recipientConfigJson(), req.priority());
        return ResponseEntity.ok(ApiResponse.success(service.updateRule(cmd)));
    }

    @Operation(summary = "Get email rule")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getRule(id)));
    }

    @Operation(summary = "Search email rules")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EmailRuleResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID workspaceId,
            @RequestParam(required = false) UUID eventDefinitionId,
            @RequestParam(required = false) UUID templateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = new SearchEmailRulesQuery(keyword, scope, status, workspaceId, eventDefinitionId, templateId, page, size);
        return ResponseEntity.ok(ApiResponse.success(service.searchRules(query)));
    }

    @Operation(summary = "Activate email rule")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.activateRule(id)));
    }

    @Operation(summary = "Deactivate email rule")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.deactivateRule(id)));
    }

    @Operation(summary = "Enable email rule")
    @PatchMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> enable(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.enableRule(id)));
    }

    @Operation(summary = "Disable email rule")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> disable(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.disableRule(id)));
    }

    @Operation(summary = "Delete email rule")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.deleteRule(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
