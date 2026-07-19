package com.company.scopery.modules.documenthub.generatedjob.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.generatedjob.application.action.*;
import com.company.scopery.modules.documenthub.generatedjob.application.command.CreateGeneratedDocumentJobCommand;
import com.company.scopery.modules.documenthub.generatedjob.application.response.GeneratedDocumentJobResponse;
import com.company.scopery.modules.documenthub.generatedjob.application.service.GeneratedDocumentJobQueryService;
import com.company.scopery.modules.documenthub.generatedjob.http.request.*;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(DocumentHubApiPaths.GENERATED_JOBS)
@Tag(name = "Document Hub - Generated Jobs")
public class GeneratedDocumentJobController {
    private final CreateGeneratedDocumentJobAction create;
    private final CompleteGeneratedDocumentJobAction complete;
    private final ProcessGeneratedDocumentJobAction process;
    private final GeneratedDocumentJobQueryService query;
    public GeneratedDocumentJobController(CreateGeneratedDocumentJobAction create, CompleteGeneratedDocumentJobAction complete,
                                          ProcessGeneratedDocumentJobAction process, GeneratedDocumentJobQueryService query) {
        this.create=create; this.complete=complete; this.process=process; this.query=query;
    }
    @PostMapping @Operation(summary = "Queue generation job")
    public ApiResponse<GeneratedDocumentJobResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateGeneratedDocumentJobRequest r) {
        return ApiResponse.success(create.execute(new CreateGeneratedDocumentJobCommand(projectId, r.templateId(), r.templateVersionId(), r.jobType(), r.sourceType(), r.sourceId())));
    }
    @GetMapping @Operation(summary = "List jobs")
    public ApiResponse<List<GeneratedDocumentJobResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{jobId}") @Operation(summary = "Get job")
    public ApiResponse<GeneratedDocumentJobResponse> get(@PathVariable UUID projectId, @PathVariable UUID jobId) {
        return ApiResponse.success(query.get(projectId, jobId));
    }
    @PostMapping("/{jobId}/process") @Operation(summary = "Claim job, render template, and store generated document")
    public ApiResponse<GeneratedDocumentJobResponse> process(@PathVariable UUID projectId, @PathVariable UUID jobId,
            @RequestBody(required = false) ProcessGeneratedDocumentJobRequest r) {
        return ApiResponse.success(process.execute(projectId, jobId, r == null ? null : r.variables()));
    }
    @PostMapping("/{jobId}/complete") @Operation(summary = "Mark job completed")
    public ApiResponse<GeneratedDocumentJobResponse> complete(@PathVariable UUID projectId, @PathVariable UUID jobId, @RequestBody CompleteGeneratedDocumentJobRequest r) {
        return ApiResponse.success(complete.execute(projectId, jobId, r.outputDocumentId()));
    }
}
