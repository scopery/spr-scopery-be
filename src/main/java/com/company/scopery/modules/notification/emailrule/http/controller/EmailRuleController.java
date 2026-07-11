package com.company.scopery.modules.notification.emailrule.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.emailrule.application.action.ActivateEmailRuleAction;
import com.company.scopery.modules.notification.emailrule.application.action.CreateEmailRuleAction;
import com.company.scopery.modules.notification.emailrule.application.action.DeactivateEmailRuleAction;
import com.company.scopery.modules.notification.emailrule.application.action.DeleteEmailRuleAction;
import com.company.scopery.modules.notification.emailrule.application.action.DisableEmailRuleAction;
import com.company.scopery.modules.notification.emailrule.application.action.EnableEmailRuleAction;
import com.company.scopery.modules.notification.emailrule.application.action.UpdateEmailRuleAction;
import com.company.scopery.modules.notification.emailrule.application.command.CreateEmailRuleCommand;
import com.company.scopery.modules.notification.emailrule.application.command.UpdateEmailRuleCommand;
import com.company.scopery.modules.notification.emailrule.application.query.SearchEmailRulesQuery;
import com.company.scopery.modules.notification.emailrule.application.response.EmailRuleResponse;
import com.company.scopery.modules.notification.emailrule.application.service.EmailRuleQueryService;
import com.company.scopery.modules.notification.emailrule.http.request.CreateEmailRuleRequest;
import com.company.scopery.modules.notification.emailrule.http.request.UpdateEmailRuleRequest;
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

    private final CreateEmailRuleAction createAction;
    private final UpdateEmailRuleAction updateAction;
    private final ActivateEmailRuleAction activateAction;
    private final DeactivateEmailRuleAction deactivateAction;
    private final EnableEmailRuleAction enableAction;
    private final DisableEmailRuleAction disableAction;
    private final DeleteEmailRuleAction deleteAction;
    private final EmailRuleQueryService queryService;

    public EmailRuleController(CreateEmailRuleAction createAction,
                                UpdateEmailRuleAction updateAction,
                                ActivateEmailRuleAction activateAction,
                                DeactivateEmailRuleAction deactivateAction,
                                EnableEmailRuleAction enableAction,
                                DisableEmailRuleAction disableAction,
                                DeleteEmailRuleAction deleteAction,
                                EmailRuleQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.enableAction = enableAction;
        this.disableAction = disableAction;
        this.deleteAction = deleteAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create email rule")
    @PostMapping
    public ResponseEntity<ApiResponse<EmailRuleResponse>> create(@Valid @RequestBody CreateEmailRuleRequest req) {
        var cmd = new CreateEmailRuleCommand(req.code(), req.name(), req.description(),
                req.scope(), req.workspaceId(), req.eventDefinitionId(), req.templateId(),
                req.recipientStrategy(), req.recipientConfigJson(), req.priority());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createAction.execute(cmd)));
    }

    @Operation(summary = "Update email rule")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateEmailRuleRequest req) {
        var cmd = new UpdateEmailRuleCommand(id, req.name(), req.description(),
                req.recipientStrategy(), req.recipientConfigJson(), req.priority());
        return ResponseEntity.ok(ApiResponse.success(updateAction.execute(cmd)));
    }

    @Operation(summary = "Get email rule")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getRule(id)));
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
        return ResponseEntity.ok(ApiResponse.success(queryService.searchRules(query)));
    }

    @Operation(summary = "Activate email rule")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(id)));
    }

    @Operation(summary = "Deactivate email rule")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(deactivateAction.execute(id)));
    }

    @Operation(summary = "Enable email rule")
    @PatchMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> enable(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(enableAction.execute(id)));
    }

    @Operation(summary = "Disable email rule")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<ApiResponse<EmailRuleResponse>> disable(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(disableAction.execute(id)));
    }

    @Operation(summary = "Delete email rule")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        deleteAction.execute(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
