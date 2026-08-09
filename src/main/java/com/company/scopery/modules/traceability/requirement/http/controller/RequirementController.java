package com.company.scopery.modules.traceability.requirement.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.requirement.application.action.*;
import com.company.scopery.modules.traceability.requirement.application.command.SetRequiresUseCaseCommand;
import com.company.scopery.modules.traceability.requirement.http.request.SetRequiresUseCaseRequest;
import com.company.scopery.modules.traceability.requirement.application.command.*;
import com.company.scopery.modules.traceability.requirement.application.response.LinkableFunctionResponse;
import com.company.scopery.modules.traceability.requirement.application.response.LinkableTestCaseResponse;
import com.company.scopery.modules.traceability.requirement.application.response.LinkableUseCaseResponse;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse;
import com.company.scopery.modules.traceability.requirement.application.service.RequirementQueryService;
import com.company.scopery.modules.traceability.requirement.http.request.BulkCreateRequirementRequest;
import com.company.scopery.modules.traceability.requirement.http.request.CreateRequirementRequest;
import com.company.scopery.modules.traceability.requirement.http.request.LinkTestCasesRequest;
import com.company.scopery.modules.traceability.requirement.http.request.UpdateRequirementRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.tracelink.application.response.BatchTraceLinkResponse;
import com.company.scopery.platform.bulkjob.BulkJobResponse;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.REQUIREMENTS)
@Tag(name = "Traceability - Requirements")
public class RequirementController {

    private final CreateRequirementAction create;
    private final UpdateRequirementAction update;
    private final ApproveRequirementAction approve;
    private final RejectRequirementAction reject;
    private final DeferRequirementAction defer;
    private final MarkImplementedRequirementAction markImplemented;
    private final ArchiveRequirementAction archive;
    private final DeleteRequirementAction delete;
    private final LinkTestCasesToRequirementAction linkTestCases;
    private final SetRequiresUseCaseAction setRequiresUseCase;
    private final RequirementQueryService query;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public RequirementController(CreateRequirementAction create, UpdateRequirementAction update,
                                  ApproveRequirementAction approve, RejectRequirementAction reject,
                                  DeferRequirementAction defer,
                                  MarkImplementedRequirementAction markImplemented,
                                  ArchiveRequirementAction archive,
                                  DeleteRequirementAction delete,
                                  LinkTestCasesToRequirementAction linkTestCases,
                                  SetRequiresUseCaseAction setRequiresUseCase,
                                  RequirementQueryService query,
                                  BulkJobService bulkJobService,
                                  ObjectMapper objectMapper) {
        this.create = create; this.update = update; this.approve = approve; this.reject = reject;
        this.defer = defer; this.markImplemented = markImplemented; this.archive = archive;
        this.delete = delete; this.linkTestCases = linkTestCases;
        this.setRequiresUseCase = setRequiresUseCase; this.query = query;
        this.bulkJobService = bulkJobService; this.objectMapper = objectMapper;
    }

    @PostMapping
    @Operation(summary = "Create requirement")
    public ApiResponse<RequirementResponse> create(@PathVariable UUID projectId,
                                                   @Valid @RequestBody CreateRequirementRequest r) {
        return ApiResponse.success(create.execute(new CreateRequirementCommand(
                projectId, r.applicationId(), r.code(), r.title(), r.description(),
                r.requirementType(), r.priority(), r.functionalItemId(), r.nonFunctionalItemId(), r.scopeItemId(), r.scopePackageId())));
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create requirements (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> bulkCreate(@PathVariable UUID projectId,
                                                   @Valid @RequestBody BulkCreateRequirementRequest r) {
        var items = r.items().stream()
                .map(i -> new CreateRequirementCommand(projectId, i.applicationId(), i.code(), i.title(),
                        i.description(), i.requirementType(), i.priority(), i.functionalItemId(),
                        i.nonFunctionalItemId(), i.scopeItemId(), i.scopePackageId()))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateRequirementCommand(projectId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateRequirementJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    @GetMapping
    @Operation(summary = "List requirements")
    public ApiResponse<List<RequirementResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{requirementId}")
    @Operation(summary = "Get requirement")
    public ApiResponse<RequirementResponse> get(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return ApiResponse.success(query.get(projectId, requirementId));
    }

    @PatchMapping("/{requirementId}")
    @Operation(summary = "Update requirement")
    public ApiResponse<RequirementResponse> update(@PathVariable UUID projectId, @PathVariable UUID requirementId,
                                                   @RequestBody UpdateRequirementRequest r) {
        return ApiResponse.success(update.execute(new UpdateRequirementCommand(
                requirementId, projectId, r.title(), r.description(), r.priority(), r.requirementType(),
                r.applicationId(), r.functionalItemId(), r.nonFunctionalItemId(), r.scopeItemId(), r.scopePackageId(),
                r.requiresUseCase())));
    }

    @PostMapping("/{requirementId}/approve")
    @Operation(summary = "Approve requirement")
    public ApiResponse<RequirementResponse> approve(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return ApiResponse.success(approve.execute(projectId, requirementId));
    }

    @PatchMapping("/{requirementId}/reject")
    @Operation(summary = "Reject requirement")
    public ApiResponse<RequirementResponse> reject(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return ApiResponse.success(reject.execute(new RejectRequirementCommand(requirementId, projectId)));
    }

    @PatchMapping("/{requirementId}/defer")
    @Operation(summary = "Defer requirement")
    public ApiResponse<RequirementResponse> defer(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return ApiResponse.success(defer.execute(new DeferRequirementCommand(requirementId, projectId)));
    }

    @PatchMapping("/{requirementId}/implement")
    @Operation(summary = "Mark requirement as implemented")
    public ApiResponse<RequirementResponse> implement(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return ApiResponse.success(markImplemented.execute(new MarkImplementedRequirementCommand(requirementId, projectId)));
    }

    @PatchMapping("/{requirementId}/archive")
    @Operation(summary = "Archive requirement")
    public ApiResponse<RequirementResponse> archive(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return ApiResponse.success(archive.execute(new ArchiveRequirementCommand(requirementId, projectId)));
    }

    @DeleteMapping("/{requirementId}")
    @Operation(summary = "Permanently delete a requirement (only if it has no active links)")
    public ApiResponse<Void> delete(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        delete.execute(new DeleteRequirementCommand(projectId, requirementId));
        return ApiResponse.success(null);
    }

    @PostMapping("/{requirementId}/test-case-links")
    @Operation(summary = "Link test cases to a requirement via TESTED_BY (idempotent)")
    public ApiResponse<BatchTraceLinkResponse> linkTestCases(@PathVariable UUID projectId,
                                                              @PathVariable UUID requirementId,
                                                              @Valid @RequestBody LinkTestCasesRequest r) {
        return ApiResponse.success(linkTestCases.execute(projectId, requirementId, r.testCaseIds()));
    }

    @GetMapping("/{requirementId}/linkable-test-cases")
    @Operation(summary = "Search test cases not yet linked to this requirement via TESTED_BY")
    public ApiResponse<List<LinkableTestCaseResponse>> linkableTestCases(
            @PathVariable UUID projectId,
            @PathVariable UUID requirementId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.success(query.linkableTestCases(projectId, requirementId, q, limit));
    }

    @PatchMapping("/{requirementId}/requires-use-case")
    @Operation(summary = "Set requiresUseCase override (YES | NO | AUTO)")
    public ApiResponse<RequirementResponse> setRequiresUseCase(@PathVariable UUID projectId,
                                                                @PathVariable UUID requirementId,
                                                                @Valid @RequestBody SetRequiresUseCaseRequest r) {
        return ApiResponse.success(setRequiresUseCase.execute(new SetRequiresUseCaseCommand(requirementId, projectId, r.value())));
    }

    @GetMapping("/{requirementId}/linkable-functions")
    @Operation(summary = "Search functions not yet linked to this requirement")
    public ApiResponse<List<LinkableFunctionResponse>> linkableFunctions(
            @PathVariable UUID projectId,
            @PathVariable UUID requirementId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.success(query.linkableFunctions(projectId, requirementId, q, limit));
    }

    @GetMapping("/{requirementId}/linkable-use-cases")
    @Operation(summary = "Search use cases not yet linked to this requirement")
    public ApiResponse<List<LinkableUseCaseResponse>> linkableUseCases(
            @PathVariable UUID projectId,
            @PathVariable UUID requirementId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.success(query.linkableUseCases(projectId, requirementId, q, limit));
    }
}
