package com.company.scopery.modules.quality.testrun.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import com.company.scopery.modules.quality.testrun.application.action.*; import com.company.scopery.modules.quality.testrun.application.command.CreateTestRunCommand;
import com.company.scopery.modules.quality.testrun.application.response.*; import com.company.scopery.modules.quality.testrun.application.service.TestRunQueryService;
import com.company.scopery.modules.quality.testrun.http.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(QualityApiPaths.TEST_RUNS) @Tag(name="Quality - Test Runs")
public class TestRunController {
    private final CreateTestRunAction create; private final StartTestRunAction start; private final CompleteTestRunAction complete;
    private final CancelTestRunAction cancel; private final RecordTestCaseResultAction recordResult; private final TestRunQueryService query;
    public TestRunController(CreateTestRunAction create, StartTestRunAction start, CompleteTestRunAction complete,
                             CancelTestRunAction cancel, RecordTestCaseResultAction recordResult, TestRunQueryService query) {
        this.create=create; this.start=start; this.complete=complete; this.cancel=cancel; this.recordResult=recordResult; this.query=query;
    }
    @PostMapping @Operation(summary="Create test run")
    public ApiResponse<TestRunResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateTestRunRequest r) {
        return ApiResponse.success(create.execute(new CreateTestRunCommand(projectId, r.name(), r.runType(), r.testPlanId(), r.testSuiteId(), r.releasePackageId())));
    }
    @GetMapping @Operation(summary="List test runs")
    public ApiResponse<List<TestRunResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{testRunId}") @Operation(summary="Get test run")
    public ApiResponse<TestRunResponse> get(@PathVariable UUID projectId, @PathVariable UUID testRunId) { return ApiResponse.success(query.get(projectId, testRunId)); }
    @PostMapping("/{testRunId}/start") @Operation(summary="Start test run")
    public ApiResponse<TestRunResponse> start(@PathVariable UUID projectId, @PathVariable UUID testRunId) { return ApiResponse.success(start.execute(projectId, testRunId)); }
    @PostMapping("/{testRunId}/complete") @Operation(summary="Complete test run")
    public ApiResponse<TestRunResponse> complete(@PathVariable UUID projectId, @PathVariable UUID testRunId) { return ApiResponse.success(complete.execute(projectId, testRunId)); }
    @PostMapping("/{testRunId}/cancel") @Operation(summary="Cancel test run")
    public ApiResponse<TestRunResponse> cancel(@PathVariable UUID projectId, @PathVariable UUID testRunId) { return ApiResponse.success(cancel.execute(projectId, testRunId)); }
    @PostMapping("/{testRunId}/case-results") @Operation(summary="Record case result")
    public ApiResponse<TestCaseResultResponse> recordResult(@PathVariable UUID projectId, @PathVariable UUID testRunId, @Valid @RequestBody RecordCaseResultRequest r) {
        return ApiResponse.success(recordResult.execute(projectId, testRunId, r.testCaseId(), r.resultStatus(), r.actualResult()));
    }
    @GetMapping("/{testRunId}/case-results") @Operation(summary="List case results")
    public ApiResponse<List<TestCaseResultResponse>> listResults(@PathVariable UUID projectId, @PathVariable UUID testRunId) {
        return ApiResponse.success(query.listResults(projectId, testRunId));
    }
}
