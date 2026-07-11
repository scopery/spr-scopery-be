package com.company.scopery.modules.notification.emailtemplate.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.emailtemplate.application.action.ActivateEmailTemplateAction;
import com.company.scopery.modules.notification.emailtemplate.application.action.CreateEmailTemplateAction;
import com.company.scopery.modules.notification.emailtemplate.application.action.CreateEmailTemplateVersionAction;
import com.company.scopery.modules.notification.emailtemplate.application.action.DeactivateEmailTemplateAction;
import com.company.scopery.modules.notification.emailtemplate.application.action.DeleteEmailTemplateAction;
import com.company.scopery.modules.notification.emailtemplate.application.action.PublishEmailTemplateVersionAction;
import com.company.scopery.modules.notification.emailtemplate.application.action.UpdateEmailTemplateAction;
import com.company.scopery.modules.notification.emailtemplate.application.command.CreateEmailTemplateCommand;
import com.company.scopery.modules.notification.emailtemplate.application.command.CreateEmailTemplateVersionCommand;
import com.company.scopery.modules.notification.emailtemplate.application.command.PublishEmailTemplateVersionCommand;
import com.company.scopery.modules.notification.emailtemplate.application.command.UpdateEmailTemplateCommand;
import com.company.scopery.modules.notification.emailtemplate.application.query.SearchEmailTemplatesQuery;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateResponse;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateVersionResponse;
import com.company.scopery.modules.notification.emailtemplate.application.service.EmailTemplateQueryService;
import com.company.scopery.modules.notification.emailtemplate.http.request.CreateEmailTemplateRequest;
import com.company.scopery.modules.notification.emailtemplate.http.request.CreateEmailTemplateVersionRequest;
import com.company.scopery.modules.notification.emailtemplate.http.request.UpdateEmailTemplateRequest;
import com.company.scopery.modules.notification.shared.NotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification - Email Templates", description = "Manage email notification templates")
@RestController
@RequestMapping(NotificationApiPaths.EMAIL_TEMPLATES)
public class EmailTemplateController {

    private final CreateEmailTemplateAction createAction;
    private final UpdateEmailTemplateAction updateAction;
    private final ActivateEmailTemplateAction activateAction;
    private final DeactivateEmailTemplateAction deactivateAction;
    private final DeleteEmailTemplateAction deleteAction;
    private final CreateEmailTemplateVersionAction createVersionAction;
    private final PublishEmailTemplateVersionAction publishVersionAction;
    private final EmailTemplateQueryService queryService;

    public EmailTemplateController(CreateEmailTemplateAction createAction,
                                    UpdateEmailTemplateAction updateAction,
                                    ActivateEmailTemplateAction activateAction,
                                    DeactivateEmailTemplateAction deactivateAction,
                                    DeleteEmailTemplateAction deleteAction,
                                    CreateEmailTemplateVersionAction createVersionAction,
                                    PublishEmailTemplateVersionAction publishVersionAction,
                                    EmailTemplateQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.deleteAction = deleteAction;
        this.createVersionAction = createVersionAction;
        this.publishVersionAction = publishVersionAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create email template")
    @PostMapping
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> create(@Valid @RequestBody CreateEmailTemplateRequest req) {
        var cmd = new CreateEmailTemplateCommand(req.code(), req.name(), req.description(),
                req.scope(), req.workspaceId(), req.eventDefinitionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createAction.execute(cmd)));
    }

    @Operation(summary = "Update email template")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateEmailTemplateRequest req) {
        var cmd = new UpdateEmailTemplateCommand(id, req.name(), req.description());
        return ResponseEntity.ok(ApiResponse.success(updateAction.execute(cmd)));
    }

    @Operation(summary = "Get email template")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getTemplate(id)));
    }

    @Operation(summary = "Search email templates")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EmailTemplateResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID workspaceId,
            @RequestParam(required = false) UUID eventDefinitionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = new SearchEmailTemplatesQuery(keyword, scope, status, workspaceId, eventDefinitionId, page, size);
        return ResponseEntity.ok(ApiResponse.success(queryService.searchTemplates(query)));
    }

    @Operation(summary = "Activate email template")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(id)));
    }

    @Operation(summary = "Deactivate email template")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(deactivateAction.execute(id)));
    }

    @Operation(summary = "Delete email template")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        deleteAction.execute(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Create draft version for template")
    @PostMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<EmailTemplateVersionResponse>> createVersion(
            @PathVariable UUID id, @Valid @RequestBody CreateEmailTemplateVersionRequest req) {
        var cmd = new CreateEmailTemplateVersionCommand(id, req.subjectTemplate(),
                req.htmlBodyTemplate(), req.textBodyTemplate());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createVersionAction.execute(cmd)));
    }

    @Operation(summary = "Publish a version")
    @PatchMapping("/{id}/versions/{versionId}/publish")
    public ResponseEntity<ApiResponse<EmailTemplateVersionResponse>> publishVersion(
            @PathVariable UUID id, @PathVariable UUID versionId) {
        var cmd = new PublishEmailTemplateVersionCommand(id, versionId);
        return ResponseEntity.ok(ApiResponse.success(publishVersionAction.execute(cmd)));
    }

    @Operation(summary = "List versions for template")
    @GetMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<List<EmailTemplateVersionResponse>>> listVersions(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getVersions(id)));
    }
}
