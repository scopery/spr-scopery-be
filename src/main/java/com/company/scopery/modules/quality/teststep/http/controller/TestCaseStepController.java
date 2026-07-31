package com.company.scopery.modules.quality.teststep.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import com.company.scopery.modules.quality.teststep.application.action.*;
import com.company.scopery.modules.quality.teststep.application.command.*;
import com.company.scopery.modules.quality.teststep.application.response.*;
import com.company.scopery.modules.quality.teststep.application.service.TestCaseStepQueryService;
import com.company.scopery.modules.quality.teststep.http.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(QualityApiPaths.TEST_CASE_STEPS)
@Tag(name = "Quality - Test Case Steps")
public class TestCaseStepController {
    private final CreateTestCaseStepAction create;
    private final UpdateTestCaseStepAction update;
    private final ArchiveTestCaseStepAction archive;
    private final DuplicateTestCaseStepAction duplicate;
    private final ReorderTestCaseStepsAction reorder;
    private final BatchCreateTestCaseStepsAction batchCreate;
    private final TestCaseStepQueryService query;
    public TestCaseStepController(CreateTestCaseStepAction create, UpdateTestCaseStepAction update,
            ArchiveTestCaseStepAction archive, DuplicateTestCaseStepAction duplicate,
            ReorderTestCaseStepsAction reorder, BatchCreateTestCaseStepsAction batchCreate,
            TestCaseStepQueryService query) {
        this.create=create; this.update=update; this.archive=archive; this.duplicate=duplicate;
        this.reorder=reorder; this.batchCreate=batchCreate; this.query=query;
    }
    @PostMapping @Operation(summary = "Create test case step")
    public ApiResponse<TestCaseStepResponse> create(@PathVariable UUID projectId, @PathVariable UUID testCaseId,
            @Valid @RequestBody CreateTestCaseStepRequest r) {
        return ApiResponse.success(create.execute(new CreateTestCaseStepCommand(projectId, testCaseId, r.action(), r.expectedResult(), r.screenId(), r.componentId())));
    }
    @GetMapping @Operation(summary = "List test case steps")
    public ApiResponse<List<TestCaseStepResponse>> list(@PathVariable UUID projectId, @PathVariable UUID testCaseId) {
        return ApiResponse.success(query.list(projectId, testCaseId));
    }
    @GetMapping("/{stepId}") @Operation(summary = "Get test case step")
    public ApiResponse<TestCaseStepResponse> get(@PathVariable UUID projectId, @PathVariable UUID testCaseId, @PathVariable UUID stepId) {
        return ApiResponse.success(query.get(projectId, stepId));
    }
    @PatchMapping("/{stepId}") @Operation(summary = "Update test case step")
    public ApiResponse<TestCaseStepResponse> update(@PathVariable UUID projectId, @PathVariable UUID testCaseId,
            @PathVariable UUID stepId, @RequestBody UpdateTestCaseStepRequest r) {
        return ApiResponse.success(update.execute(new UpdateTestCaseStepCommand(projectId, testCaseId, stepId, r.action(), r.expectedResult(), r.screenId(), r.componentId(), r.version())));
    }
    @PatchMapping("/{stepId}/archive") @Operation(summary = "Archive test case step")
    public ApiResponse<TestCaseStepResponse> archive(@PathVariable UUID projectId, @PathVariable UUID testCaseId, @PathVariable UUID stepId) {
        return ApiResponse.success(archive.execute(projectId, stepId));
    }
    @PostMapping("/{stepId}/duplicate") @Operation(summary = "Duplicate test case step")
    public ApiResponse<TestCaseStepResponse> duplicate(@PathVariable UUID projectId, @PathVariable UUID testCaseId, @PathVariable UUID stepId) {
        return ApiResponse.success(duplicate.execute(projectId, stepId));
    }
    @PostMapping("/reorder") @Operation(summary = "Reorder test case steps")
    public ApiResponse<List<TestCaseStepResponse>> reorder(@PathVariable UUID projectId, @PathVariable UUID testCaseId,
            @Valid @RequestBody ReorderTestCaseStepsRequest r) {
        return ApiResponse.success(reorder.execute(new ReorderTestCaseStepsCommand(projectId, testCaseId, r.orderedStepIds())));
    }
    @PostMapping("/batch") @Operation(summary = "Batch create test case steps")
    public ApiResponse<BatchCreateStepsResult> batchCreate(@PathVariable UUID projectId, @PathVariable UUID testCaseId,
            @Valid @RequestBody BatchCreateTestCaseStepsRequest r) {
        var items = r.items().stream().map(i -> new BatchCreateTestCaseStepsCommand.StepItem(i.action(), i.expectedResult(), i.screenId(), i.componentId())).toList();
        return ApiResponse.success(batchCreate.execute(new BatchCreateTestCaseStepsCommand(projectId, testCaseId, items)));
    }
}
