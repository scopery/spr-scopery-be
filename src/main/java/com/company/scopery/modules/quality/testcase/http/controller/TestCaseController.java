package com.company.scopery.modules.quality.testcase.http.controller;
import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import com.company.scopery.modules.quality.testcase.application.action.*;
import com.company.scopery.modules.quality.testcase.application.command.*;
import com.company.scopery.modules.quality.testcase.application.response.*;
import com.company.scopery.modules.quality.testcase.application.service.TestCaseQueryService;
import com.company.scopery.modules.quality.testcase.http.request.*;
import com.company.scopery.platform.bulkjob.BulkJobResponse;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping(QualityApiPaths.TEST_CASES) @Tag(name="Quality - Test Cases")
public class TestCaseController {
    private final CreateTestCaseAction create;
    private final UpdateTestCaseAction update;
    private final ApproveTestCaseAction approve;
    private final ArchiveTestCaseAction archive;
    private final BatchUpdateTestCasesAction batchUpdate;
    private final BatchCreateTestCasesAction batchCreate;
    private final UpdateRequirementLinksAction updateReqLinks;
    private final UpdateUseCaseLinksAction updateUcLinks;
    private final TestCaseQueryService query;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;
    public TestCaseController(CreateTestCaseAction create, UpdateTestCaseAction update,
            ApproveTestCaseAction approve, ArchiveTestCaseAction archive,
            BatchUpdateTestCasesAction batchUpdate, BatchCreateTestCasesAction batchCreate,
            UpdateRequirementLinksAction updateReqLinks, UpdateUseCaseLinksAction updateUcLinks,
            TestCaseQueryService query, BulkJobService bulkJobService, ObjectMapper objectMapper) {
        this.create=create; this.update=update; this.approve=approve; this.archive=archive;
        this.batchUpdate=batchUpdate; this.batchCreate=batchCreate;
        this.updateReqLinks=updateReqLinks; this.updateUcLinks=updateUcLinks; this.query=query;
        this.bulkJobService=bulkJobService; this.objectMapper=objectMapper;
    }
    @PostMapping @Operation(summary="Create test case")
    public ApiResponse<TestCaseResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateTestCaseRequest r) {
        return ApiResponse.success(create.execute(toCreateCommand(projectId, r)));
    }
    @GetMapping @Operation(summary="List test cases")
    public ApiResponse<PageResponse<TestCaseListItemResponse>> list(@PathVariable UUID projectId,
            @RequestParam(required=false) String q,
            @RequestParam(required=false) String type,
            @RequestParam(required=false) String priority,
            @RequestParam(required=false) String status,
            @RequestParam(required=false) UUID assigneeId,
            @RequestParam(required=false) String automationStatus,
            @RequestParam(required=false) UUID requirementId,
            @RequestParam(required=false) UUID useCaseId,
            @RequestParam(required=false) String latestResult,
            @RequestParam(required=false) Boolean hasOpenDefect,
            @RequestParam(defaultValue="updatedAt,desc") String sort,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        return ApiResponse.success(query.list(projectId, q, type, priority, status, assigneeId, automationStatus, requirementId, useCaseId, latestResult, hasOpenDefect, sort, page, size));
    }
    @GetMapping("/{testCaseId}") @Operation(summary="Get test case detail")
    public ApiResponse<TestCaseDetailResponse> get(@PathVariable UUID projectId, @PathVariable UUID testCaseId) {
        return ApiResponse.success(query.getDetail(projectId, testCaseId));
    }
    @PatchMapping("/{testCaseId}") @Operation(summary="Update test case")
    public ApiResponse<TestCaseResponse> update(@PathVariable UUID projectId, @PathVariable UUID testCaseId,
            @RequestBody UpdateTestCaseRequest r) {
        return ApiResponse.success(update.execute(new UpdateTestCaseCommand(projectId, testCaseId, r.title(), r.description(), r.code(), r.type(), r.priority(), r.status(), r.useCaseId(), r.assigneeId(), r.automationStatus(), r.preconditions(), r.expectedResult(), r.version())));
    }
    @PostMapping("/{testCaseId}/approve") @Operation(summary="Approve test case")
    public ApiResponse<TestCaseResponse> approve(@PathVariable UUID projectId, @PathVariable UUID testCaseId) { return ApiResponse.success(approve.execute(projectId, testCaseId)); }
    @PatchMapping("/{testCaseId}/archive") @Operation(summary="Archive test case")
    public ApiResponse<TestCaseResponse> archive(@PathVariable UUID projectId, @PathVariable UUID testCaseId) { return ApiResponse.success(archive.execute(projectId, testCaseId)); }
    @PatchMapping("/batch") @Operation(summary="Batch update test cases")
    public ApiResponse<BatchUpdateResult> batchUpdate(@PathVariable UUID projectId, @Valid @RequestBody BatchUpdateTestCasesRequest r) {
        var changes = r.changes();
        return ApiResponse.success(batchUpdate.execute(new BatchUpdateTestCasesCommand(projectId, r.testCaseIds(),
                changes != null ? changes.priority() : null,
                changes != null ? changes.status() : null,
                changes != null ? changes.assigneeId() : null,
                changes != null ? changes.automationStatus() : null)));
    }
    @PostMapping("/batch") @Operation(summary="Batch create test cases")
    public ApiResponse<BatchCreateResult> batchCreate(@PathVariable UUID projectId, @Valid @RequestBody BatchCreateTestCasesRequest r) {
        var commands = r.items().stream().map(i -> new CreateTestCaseCommand(projectId, i.testSuiteId(), i.useCaseId(), i.code(), i.title(), i.description(), i.type(), i.priority(), i.preconditions(), i.expectedResult(), i.assigneeId(), i.automationStatus())).toList();
        return ApiResponse.success(batchCreate.execute(projectId, commands));
    }
    @PostMapping("/bulk") @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary="Bulk create test cases (async — poll GET /api/bulk-jobs/{id} for status). Optional steps[] are applied by BE after each shell.")
    public ApiResponse<BulkJobResponse> bulkCreate(@PathVariable UUID projectId, @Valid @RequestBody BulkCreateTestCasesRequest r) {
        var items = r.items().stream().map(i -> toCreateCommand(projectId, i)).toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateTestCasesCommand(projectId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateTestCasesJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    private static CreateTestCaseCommand toCreateCommand(UUID projectId, CreateTestCaseRequest r) {
        var steps = r.steps() == null
                ? null
                : r.steps().stream()
                        .map(s -> new CreateTestCaseCommand.NestedStep(
                                s.action(), s.expectedResult(), s.screenId(), s.componentId()))
                        .toList();
        return new CreateTestCaseCommand(
                projectId,
                r.testSuiteId(),
                r.useCaseId(),
                r.code(),
                r.title(),
                r.description(),
                r.type(),
                r.priority(),
                r.preconditions(),
                r.expectedResult(),
                r.assigneeId(),
                r.automationStatus(),
                steps);
    }
    @GetMapping("/{testCaseId}/traceability") @Operation(summary="Get test case traceability")
    public ApiResponse<TestCaseTraceabilityResponse> traceability(@PathVariable UUID projectId, @PathVariable UUID testCaseId) {
        return ApiResponse.success(query.getTraceability(projectId, testCaseId));
    }
    @PutMapping("/{testCaseId}/requirement-links") @Operation(summary="Update requirement links")
    public ApiResponse<TestCaseTraceabilityResponse> updateReqLinks(@PathVariable UUID projectId, @PathVariable UUID testCaseId,
            @RequestBody UpdateRequirementLinksRequest r) {
        return ApiResponse.success(updateReqLinks.execute(projectId, testCaseId, r.requirementIds()));
    }
    @PutMapping("/{testCaseId}/use-case-links") @Operation(summary="Update use case links")
    public ApiResponse<TestCaseTraceabilityResponse> updateUcLinks(@PathVariable UUID projectId, @PathVariable UUID testCaseId,
            @RequestBody UpdateUseCaseLinksRequest r) {
        return ApiResponse.success(updateUcLinks.execute(projectId, testCaseId, r.useCaseIds()));
    }
}
