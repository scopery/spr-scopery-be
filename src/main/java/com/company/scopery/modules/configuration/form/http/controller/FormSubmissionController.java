package com.company.scopery.modules.configuration.form.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.form.application.response.FormSubmissionResponse;
import com.company.scopery.modules.configuration.form.application.service.FormQueryService;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.FORM_SUBMISSIONS) @Tag(name = "Configuration - Form Submissions")
public class FormSubmissionController {
    private final FormQueryService query;
    public FormSubmissionController(FormQueryService query) { this.query=query; }
    @GetMapping @Operation(summary="List form submissions")
    public ApiResponse<List<FormSubmissionResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.listSubmissions(workspaceId)); }
    @GetMapping("/{submissionId}") @Operation(summary="Get form submission")
    public ApiResponse<FormSubmissionResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID submissionId) {
        return ApiResponse.success(query.getSubmission(workspaceId, submissionId));
    }
}
