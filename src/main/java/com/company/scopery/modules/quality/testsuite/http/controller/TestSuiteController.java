package com.company.scopery.modules.quality.testsuite.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.testsuite.application.action.ArchiveTestSuiteAction;
import com.company.scopery.modules.quality.testsuite.application.action.CreateTestSuiteAction;
import com.company.scopery.modules.quality.testsuite.application.command.CreateTestSuiteCommand;
import com.company.scopery.modules.quality.testsuite.application.response.TestSuiteResponse;
import com.company.scopery.modules.quality.testsuite.application.service.TestSuiteQueryService;
import com.company.scopery.modules.quality.testsuite.http.request.CreateTestSuiteRequest;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@RestController
@RequestMapping(QualityApiPaths.TEST_PLAN_SUITES)
@Tag(name = "Quality - Test Suites")
public class TestSuiteController {
    private final CreateTestSuiteAction create;
    private final ArchiveTestSuiteAction archive;
    private final TestSuiteQueryService query;
    public TestSuiteController(CreateTestSuiteAction create, ArchiveTestSuiteAction archive, TestSuiteQueryService query) {
        this.create = create; this.archive = archive; this.query = query;
    }
    @PostMapping @Operation(summary = "Create test suite")
    public ApiResponse<TestSuiteResponse> create(@PathVariable UUID projectId, @PathVariable UUID testPlanId,
                                                 @Valid @RequestBody CreateTestSuiteRequest r) {
        return ApiResponse.success(create.execute(new CreateTestSuiteCommand(
                projectId, testPlanId, r.name(), r.description(), r.deliverableId(), r.scopeItemId(), r.sortOrder())));
    }
    @GetMapping @Operation(summary = "List suites for test plan")
    public ApiResponse<List<TestSuiteResponse>> list(@PathVariable UUID projectId, @PathVariable UUID testPlanId) {
        return ApiResponse.success(query.listByTestPlan(projectId, testPlanId));
    }
    @GetMapping("/{suiteId}") @Operation(summary = "Get test suite")
    public ApiResponse<TestSuiteResponse> get(@PathVariable UUID projectId, @PathVariable UUID suiteId) {
        return ApiResponse.success(query.get(projectId, suiteId));
    }
    @PatchMapping("/{suiteId}/archive") @Operation(summary = "Archive test suite")
    public ApiResponse<TestSuiteResponse> archive(@PathVariable UUID projectId, @PathVariable UUID suiteId) {
        return ApiResponse.success(archive.execute(projectId, suiteId));
    }
}
