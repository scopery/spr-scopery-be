package com.company.scopery.modules.traceability.requirement.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.requirement.application.action.*;
import com.company.scopery.modules.traceability.requirement.application.command.*;
import com.company.scopery.modules.traceability.requirement.application.response.LinkableTestCaseResponse;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse;
import com.company.scopery.modules.traceability.requirement.application.service.RequirementQueryService;
import com.company.scopery.modules.traceability.requirement.http.request.CreateRequirementRequest;
import com.company.scopery.modules.traceability.requirement.http.request.LinkTestCasesRequest;
import com.company.scopery.modules.traceability.requirement.http.request.UpdateRequirementRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.tracelink.application.response.BatchTraceLinkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final LinkTestCasesToRequirementAction linkTestCases;
    private final RequirementQueryService query;

    public RequirementController(CreateRequirementAction create, UpdateRequirementAction update,
                                  ApproveRequirementAction approve, RejectRequirementAction reject,
                                  DeferRequirementAction defer,
                                  MarkImplementedRequirementAction markImplemented,
                                  ArchiveRequirementAction archive,
                                  LinkTestCasesToRequirementAction linkTestCases,
                                  RequirementQueryService query) {
        this.create = create; this.update = update; this.approve = approve; this.reject = reject;
        this.defer = defer; this.markImplemented = markImplemented; this.archive = archive;
        this.linkTestCases = linkTestCases; this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create requirement")
    public ApiResponse<RequirementResponse> create(@PathVariable UUID projectId,
                                                   @Valid @RequestBody CreateRequirementRequest r) {
        return ApiResponse.success(create.execute(new CreateRequirementCommand(
                projectId, r.applicationId(), r.code(), r.title(), r.description(),
                r.requirementType(), r.priority(), r.functionalItemId(), r.nonFunctionalItemId(), r.scopeItemId(), r.scopePackageId())));
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
                r.applicationId(), r.functionalItemId(), r.nonFunctionalItemId(), r.scopeItemId(), r.scopePackageId())));
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
}
