package com.company.scopery.modules.notification.emailtemplate.api;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.notification.emailtemplate.api.request.PreviewEmailTemplateRequest;
import com.company.scopery.modules.notification.emailtemplate.application.EmailTemplateApplicationService;
import com.company.scopery.modules.notification.emailtemplate.application.EmailTemplateRenderer;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateVersionResponse;
import com.company.scopery.modules.notification.shared.NotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Notification - Email Templates", description = "Manage email notification templates")
@RestController
@RequestMapping(NotificationApiPaths.EMAIL_TEMPLATES + "/preview")
public class EmailTemplatePreviewController {

    private final EmailTemplateApplicationService templateService;
    private final EmailTemplateRenderer renderer;

    public EmailTemplatePreviewController(EmailTemplateApplicationService templateService,
                                           EmailTemplateRenderer renderer) {
        this.templateService = templateService;
        this.renderer = renderer;
    }

    @Operation(summary = "Preview a template version with sample payload")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> preview(
            @Valid @RequestBody PreviewEmailTemplateRequest req) {
        EmailTemplateVersionResponse version = templateService.getVersion(req.versionId());
        Map<String, Object> payload = req.samplePayload() != null ? req.samplePayload() : Map.of();
        String renderedSubject = renderer.render(version.subjectTemplate(), payload);
        String renderedHtml = renderer.render(version.htmlBodyTemplate(), payload);
        String renderedText = renderer.render(version.textBodyTemplate(), payload);
        Map<String, String> result = Map.of(
                "subject", renderedSubject != null ? renderedSubject : "",
                "htmlBody", renderedHtml != null ? renderedHtml : "",
                "textBody", renderedText != null ? renderedText : "");
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
