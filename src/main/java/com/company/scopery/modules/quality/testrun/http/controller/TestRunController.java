package com.company.scopery.modules.quality.testrun.http.controller;
import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testrun.application.action.*;
import com.company.scopery.modules.quality.testrun.application.command.*;
import com.company.scopery.modules.quality.testrun.application.response.*;
import com.company.scopery.modules.quality.testrun.application.service.TestRunQueryService;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import com.company.scopery.modules.quality.testrun.http.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(QualityApiPaths.TEST_RUNS) @Tag(name="Quality - Test Runs")
public class TestRunController {
    private final CreateTestRunAction create; private final StartTestRunAction start;
    private final CompleteTestRunAction complete; private final CancelTestRunAction cancel;
    private final RecordTestCaseResultAction recordResult; private final UpdateTestCaseResultAction updateResult;
    private final BatchUpdateResultsAction batchUpdateResults;
    private final ManageRunMembershipAction manageMembership; private final CopyRunMembershipAction copyMembership;
    private final TestRunQueryService query;
    public TestRunController(CreateTestRunAction create, StartTestRunAction start, CompleteTestRunAction complete,
                             CancelTestRunAction cancel, RecordTestCaseResultAction recordResult,
                             UpdateTestCaseResultAction updateResult, BatchUpdateResultsAction batchUpdateResults,
                             ManageRunMembershipAction manageMembership, CopyRunMembershipAction copyMembership,
                             TestRunQueryService query) {
        this.create=create; this.start=start; this.complete=complete; this.cancel=cancel;
        this.recordResult=recordResult; this.updateResult=updateResult;
        this.batchUpdateResults=batchUpdateResults;
        this.manageMembership=manageMembership; this.copyMembership=copyMembership;
        this.query=query;
    }
    @PostMapping @Operation(summary="Create test run")
    public ApiResponse<TestRunResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateTestRunRequest r) {
        List<CreateTestRunCommand.CaseRef> refs = r.caseIds() == null ? List.of() :
                r.caseIds().stream().filter(c -> c.caseKind() != null && c.caseId() != null)
                        .map(c -> new CreateTestRunCommand.CaseRef(QualityEnumParser.parseRequired(MembershipCaseKind.class, c.caseKind(), "caseKind"), c.caseId()))
                        .toList();
        return ApiResponse.success(create.execute(new CreateTestRunCommand(projectId, r.name(), r.runType(), r.runScope(), r.testPlanId(), r.testSuiteId(), r.releasePackageId(), refs)));
    }
    @GetMapping @Operation(summary="List test runs")
    public ApiResponse<PageResponse<TestRunListItemResponse>> list(@PathVariable UUID projectId,
            @RequestParam(required=false) String q, @RequestParam(required=false) String status,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size) {
        return ApiResponse.success(query.listWithProgress(projectId, q, status, page, size));
    }
    @GetMapping("/{testRunId}") @Operation(summary="Get test run")
    public ApiResponse<TestRunResponse> get(@PathVariable UUID projectId, @PathVariable UUID testRunId) { return ApiResponse.success(query.get(projectId, testRunId)); }
    @PostMapping("/{testRunId}/start") @Operation(summary="Start test run")
    public ApiResponse<TestRunResponse> start(@PathVariable UUID projectId, @PathVariable UUID testRunId) { return ApiResponse.success(start.execute(projectId, testRunId)); }
    @PostMapping("/{testRunId}/complete") @Operation(summary="Complete test run")
    public ApiResponse<TestRunResponse> complete(@PathVariable UUID projectId, @PathVariable UUID testRunId) { return ApiResponse.success(complete.execute(projectId, testRunId)); }
    @PostMapping("/{testRunId}/cancel") @Operation(summary="Cancel test run")
    public ApiResponse<TestRunResponse> cancel(@PathVariable UUID projectId, @PathVariable UUID testRunId) { return ApiResponse.success(cancel.execute(projectId, testRunId)); }
    @GetMapping("/{testRunId}/membership") @Operation(summary="Get run membership (cases added to this run)")
    public ApiResponse<RunMembershipResponse> getMembership(@PathVariable UUID projectId, @PathVariable UUID testRunId) {
        return ApiResponse.success(query.getMembership(projectId, testRunId));
    }
    @PutMapping("/{testRunId}/membership") @Operation(summary="Add or remove cases from run membership")
    public ApiResponse<RunMembershipResponse> manageMembership(@PathVariable UUID projectId, @PathVariable UUID testRunId,
            @Valid @RequestBody ManageRunMembershipRequest r) {
        List<ManageRunMembershipCommand.CaseRef> adds = r.add() == null ? List.of() :
                r.add().stream().filter(c -> c.caseKind() != null && c.caseId() != null)
                        .map(c -> new ManageRunMembershipCommand.CaseRef(QualityEnumParser.parseRequired(MembershipCaseKind.class, c.caseKind(), "caseKind"), c.caseId()))
                        .toList();
        List<ManageRunMembershipCommand.CaseRef> removes = r.remove() == null ? List.of() :
                r.remove().stream().filter(c -> c.caseKind() != null && c.caseId() != null)
                        .map(c -> new ManageRunMembershipCommand.CaseRef(QualityEnumParser.parseRequired(MembershipCaseKind.class, c.caseKind(), "caseKind"), c.caseId()))
                        .toList();
        return ApiResponse.success(manageMembership.execute(new ManageRunMembershipCommand(projectId, testRunId, adds, removes)));
    }
    @PostMapping("/{testRunId}/membership/copy") @Operation(summary="Copy membership from another run")
    public ApiResponse<RunMembershipResponse> copyMembership(@PathVariable UUID projectId, @PathVariable UUID testRunId,
            @Valid @RequestBody CopyRunMembershipRequest r) {
        return ApiResponse.success(copyMembership.execute(new CopyRunMembershipCommand(projectId, testRunId, r.sourceRunId(), r.replaceExisting())));
    }
    @PostMapping("/{testRunId}/case-results") @Operation(summary="Record case result")
    public ApiResponse<TestCaseResultResponse> recordResult(@PathVariable UUID projectId, @PathVariable UUID testRunId, @Valid @RequestBody RecordCaseResultRequest r) {
        return ApiResponse.success(recordResult.execute(projectId, testRunId, r.testCaseId(), r.resultStatus(), r.actualResult()));
    }
    @GetMapping("/{testRunId}/case-results") @Operation(summary="List case results (legacy)")
    public ApiResponse<List<TestCaseResultResponse>> listResultsLegacy(@PathVariable UUID projectId, @PathVariable UUID testRunId) {
        return ApiResponse.success(query.listResults(projectId, testRunId));
    }
    @GetMapping("/{testRunId}/results") @Operation(summary="List results with filters")
    public ApiResponse<PageResponse<TestCaseResultResponse>> listResults(@PathVariable UUID projectId, @PathVariable UUID testRunId,
            @RequestParam(required=false) String q, @RequestParam(required=false) String result,
            @RequestParam(required=false) UUID assigneeId, @RequestParam(required=false) Boolean hasDefect,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size) {
        return ApiResponse.success(query.listResultsPaged(projectId, testRunId, q, result, assigneeId, hasDefect, page, size));
    }
    @PatchMapping("/{testRunId}/results/{resultId}") @Operation(summary="Update test case result")
    public ApiResponse<TestCaseResultResponse> updateResult(@PathVariable UUID projectId, @PathVariable UUID testRunId,
            @PathVariable UUID resultId, @Valid @RequestBody UpdateTestCaseResultRequest r) {
        return ApiResponse.success(updateResult.execute(new UpdateTestCaseResultCommand(projectId, testRunId, resultId, r.result(), r.comment(), r.version())));
    }
    @PatchMapping("/{testRunId}/results/batch") @Operation(summary="Batch update results")
    public ApiResponse<BatchUpdateResultsResponse> batchUpdateResults(@PathVariable UUID projectId, @PathVariable UUID testRunId,
            @Valid @RequestBody BatchUpdateResultsRequest r) {
        var resultStr = r.changes() != null ? r.changes().result() : null;
        var assigneeId = r.changes() != null ? r.changes().assigneeId() : null;
        return ApiResponse.success(batchUpdateResults.execute(projectId, r.resultIds(), resultStr, assigneeId));
    }
}
