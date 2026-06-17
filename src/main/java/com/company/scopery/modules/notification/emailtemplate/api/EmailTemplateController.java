package com.company.scopery.modules.notification.emailtemplate.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.emailtemplate.api.request.*;
import com.company.scopery.modules.notification.emailtemplate.application.EmailTemplateApplicationService;
import com.company.scopery.modules.notification.emailtemplate.application.command.*;
import com.company.scopery.modules.notification.emailtemplate.application.query.SearchEmailTemplatesQuery;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateResponse;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateVersionResponse;
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

    private final EmailTemplateApplicationService service;

    public EmailTemplateController(EmailTemplateApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create email template")
    @PostMapping
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> create(@Valid @RequestBody CreateEmailTemplateRequest req) {
        var cmd = new CreateEmailTemplateCommand(req.code(), req.name(), req.description(),
                req.scope(), req.workspaceId(), req.eventDefinitionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.createTemplate(cmd)));
    }

    @Operation(summary = "Update email template")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateEmailTemplateRequest req) {
        var cmd = new UpdateEmailTemplateCommand(id, req.name(), req.description());
        return ResponseEntity.ok(ApiResponse.success(service.updateTemplate(cmd)));
    }

    @Operation(summary = "Get email template")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getTemplate(id)));
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
        return ResponseEntity.ok(ApiResponse.success(service.searchTemplates(query)));
    }

    @Operation(summary = "Activate email template")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.activateTemplate(id)));
    }

    @Operation(summary = "Deactivate email template")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.deactivateTemplate(id)));
    }

    @Operation(summary = "Delete email template")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Create draft version for template")
    @PostMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<EmailTemplateVersionResponse>> createVersion(
            @PathVariable UUID id, @Valid @RequestBody CreateEmailTemplateVersionRequest req) {
        var cmd = new CreateEmailTemplateVersionCommand(id, req.subjectTemplate(),
                req.htmlBodyTemplate(), req.textBodyTemplate());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.createVersion(cmd)));
    }

    @Operation(summary = "Publish a version")
    @PatchMapping("/{id}/versions/{versionId}/publish")
    public ResponseEntity<ApiResponse<EmailTemplateVersionResponse>> publishVersion(
            @PathVariable UUID id, @PathVariable UUID versionId) {
        var cmd = new PublishEmailTemplateVersionCommand(id, versionId);
        return ResponseEntity.ok(ApiResponse.success(service.publishVersion(cmd)));
    }

    @Operation(summary = "List versions for template")
    @GetMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<List<EmailTemplateVersionResponse>>> listVersions(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getVersions(id)));
    }
}
