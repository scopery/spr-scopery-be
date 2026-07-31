package com.company.scopery.modules.quality.verificationcase.http.controller;
import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import com.company.scopery.modules.quality.verificationcase.application.action.*;
import com.company.scopery.modules.quality.verificationcase.application.command.*;
import com.company.scopery.modules.quality.verificationcase.application.response.*;
import com.company.scopery.modules.quality.verificationcase.application.service.VerificationCaseQueryService;
import com.company.scopery.modules.quality.verificationcase.http.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping(QualityApiPaths.VERIFICATION_CASES) @Tag(name = "Quality - Verification Cases")
public class VerificationCaseController {
    private final CreateVerificationCaseAction create;
    private final UpdateVerificationCaseAction update;
    private final ArchiveVerificationCaseAction archive;
    private final VerificationCaseQueryService query;
    public VerificationCaseController(CreateVerificationCaseAction create, UpdateVerificationCaseAction update,
            ArchiveVerificationCaseAction archive, VerificationCaseQueryService query) {
        this.create = create; this.update = update; this.archive = archive; this.query = query;
    }
    @PostMapping @Operation(summary = "Create verification case")
    public ApiResponse<VerificationCaseResponse> create(@PathVariable UUID projectId,
            @Valid @RequestBody CreateVerificationCaseRequest r) {
        return ApiResponse.success(create.execute(new CreateVerificationCaseCommand(
                projectId, r.requirementId(), r.code(), r.title(), r.description(),
                r.verificationMethod(), r.procedure(), r.expectedResultJson(),
                r.environment(), r.automationStatus(), r.ownerId(), r.assigneeId())));
    }
    @GetMapping @Operation(summary = "List verification cases")
    public ApiResponse<PageResponse<VerificationCaseListItemResponse>> list(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID requirementId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID assigneeId,
            @RequestParam(defaultValue = "updatedAt,desc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(query.list(projectId, requirementId, status, assigneeId, sort, page, size));
    }
    @GetMapping("/{verificationCaseId}") @Operation(summary = "Get verification case detail")
    public ApiResponse<VerificationCaseResponse> get(@PathVariable UUID projectId,
            @PathVariable UUID verificationCaseId) {
        return ApiResponse.success(query.get(projectId, verificationCaseId));
    }
    @PatchMapping("/{verificationCaseId}") @Operation(summary = "Update verification case")
    public ApiResponse<VerificationCaseResponse> update(@PathVariable UUID projectId,
            @PathVariable UUID verificationCaseId, @RequestBody UpdateVerificationCaseRequest r) {
        return ApiResponse.success(update.execute(new UpdateVerificationCaseCommand(
                projectId, verificationCaseId, r.title(), r.description(),
                r.verificationMethod(), r.procedure(), r.expectedResultJson(),
                r.environment(), r.lifecycleStatus(), r.automationStatus(),
                r.ownerId(), r.assigneeId(), r.version())));
    }
    @PostMapping("/{verificationCaseId}/archive") @Operation(summary = "Archive verification case")
    public ApiResponse<VerificationCaseResponse> archive(@PathVariable UUID projectId,
            @PathVariable UUID verificationCaseId) {
        return ApiResponse.success(archive.execute(projectId, verificationCaseId));
    }
}
