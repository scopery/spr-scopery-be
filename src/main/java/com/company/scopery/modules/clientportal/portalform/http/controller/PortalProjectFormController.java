package com.company.scopery.modules.clientportal.portalform.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.portalform.application.service.PortalFormQueryService;
import com.company.scopery.modules.clientportal.portalform.http.request.SubmitPortalFormRequest;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import com.company.scopery.modules.configuration.form.application.action.SubmitPortalFormAction;
import com.company.scopery.modules.configuration.form.application.command.SubmitPortalFormCommand;
import com.company.scopery.modules.configuration.form.application.response.CustomFormDefinitionResponse;
import com.company.scopery.modules.configuration.form.application.response.CustomFormVersionResponse;
import com.company.scopery.modules.configuration.form.application.response.FormSubmissionResponse;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@RestController
@RequestMapping(ClientPortalApiPaths.PORTAL_PROJECTS + "/{projectId}/forms")
@Tag(name = "Client Portal - Forms")
public class PortalProjectFormController {
    private final PortalFormQueryService query;
    private final SubmitPortalFormAction submit;

    public PortalProjectFormController(PortalFormQueryService query, SubmitPortalFormAction submit) {
        this.query = query; this.submit = submit;
    }

    @GetMapping @Operation(summary = "List client-facing published forms for project")
    public ApiResponse<List<CustomFormDefinitionResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listPublished(projectId));
    }

    @GetMapping("/{formId}/published-version") @Operation(summary = "Get published form version")
    public ApiResponse<CustomFormVersionResponse> published(@PathVariable UUID projectId, @PathVariable UUID formId) {
        return ApiResponse.success(query.getPublishedVersion(projectId, formId));
    }

    @PostMapping("/{formId}/submit") @Operation(summary = "Submit form as portal user")
    public ApiResponse<FormSubmissionResponse> submit(@PathVariable UUID projectId, @PathVariable UUID formId,
                                                      @Valid @RequestBody SubmitPortalFormRequest r) {
        return ApiResponse.success(submit.execute(new SubmitPortalFormCommand(projectId, formId, r.formVersionId(), r.objectTypeCode(), r.targetId(), r.payloadJson())));
    }
}
