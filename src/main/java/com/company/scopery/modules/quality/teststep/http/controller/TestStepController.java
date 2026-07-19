package com.company.scopery.modules.quality.teststep.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.teststep.application.action.CreateTestStepAction;
import com.company.scopery.modules.quality.teststep.application.action.ArchiveTestStepAction;
import com.company.scopery.modules.quality.teststep.application.command.CreateTestStepCommand;
import com.company.scopery.modules.quality.teststep.application.response.TestStepResponse;
import com.company.scopery.modules.quality.teststep.application.service.TestStepQueryService;
import com.company.scopery.modules.quality.teststep.http.request.CreateTestStepRequest;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(QualityApiPaths.TEST_CASE_STEPS)
@Tag(name = "Quality - Test Steps")
public class TestStepController {
    private final CreateTestStepAction create;
    private final ArchiveTestStepAction archive;
    private final TestStepQueryService query;
    public TestStepController(CreateTestStepAction create, ArchiveTestStepAction archive, TestStepQueryService query) {
        this.create=create; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary = "Create")
    public ApiResponse<TestStepResponse> create(@PathVariable UUID projectId, @PathVariable UUID testCaseId, @Valid @RequestBody CreateTestStepRequest r) {
        return ApiResponse.success(create.execute(new CreateTestStepCommand(projectId, testCaseId, r.stepOrder(), r.actionText(), r.expectedResult(), r.dataNotes())));
    }
    @GetMapping @Operation(summary = "List")
    public ApiResponse<List<TestStepResponse>> list(@PathVariable UUID projectId, @PathVariable UUID testCaseId) {
        return ApiResponse.success(query.listByParent(projectId, testCaseId));
    }
    @GetMapping("/{stepId}") @Operation(summary = "Get")
    public ApiResponse<TestStepResponse> get(@PathVariable UUID projectId, @PathVariable UUID stepId) {
        return ApiResponse.success(query.get(projectId, stepId));
    }
    @PatchMapping("/{stepId}/archive") @Operation(summary = "Archive")
    public ApiResponse<TestStepResponse> archive(@PathVariable UUID projectId, @PathVariable UUID testCaseId, @PathVariable UUID stepId) {
        return ApiResponse.success(archive.execute(projectId, stepId));
    }
}
