package com.company.scopery.modules.quality.coverage.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.coverage.application.action.ArchiveTestCaseCoverageAction;
import com.company.scopery.modules.quality.coverage.application.action.CreateTestCaseCoverageAction;
import com.company.scopery.modules.quality.coverage.application.command.CreateTestCaseCoverageCommand;
import com.company.scopery.modules.quality.coverage.application.response.TestCaseCoverageResponse;
import com.company.scopery.modules.quality.coverage.application.service.TestCaseCoverageQueryService;
import com.company.scopery.modules.quality.coverage.http.request.CreateTestCaseCoverageRequest;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(QualityApiPaths.TEST_CASE_COVERAGE)
@Tag(name = "Quality - Test Case Coverage")
public class TestCaseCoverageController {
    private final CreateTestCaseCoverageAction create;
    private final ArchiveTestCaseCoverageAction archive;
    private final TestCaseCoverageQueryService query;
    public TestCaseCoverageController(CreateTestCaseCoverageAction create, ArchiveTestCaseCoverageAction archive, TestCaseCoverageQueryService query) {
        this.create=create; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary = "Add coverage link")
    public ApiResponse<TestCaseCoverageResponse> create(@PathVariable UUID projectId, @PathVariable UUID testCaseId,
                                                @Valid @RequestBody CreateTestCaseCoverageRequest r) {
        return ApiResponse.success(create.execute(new CreateTestCaseCoverageCommand(projectId, testCaseId, r.targetType(), r.targetId(), r.coverageType())));
    }
    @GetMapping @Operation(summary = "List coverage")
    public ApiResponse<List<TestCaseCoverageResponse>> list(@PathVariable UUID projectId, @PathVariable UUID testCaseId) {
        return ApiResponse.success(query.listByTestCase(projectId, testCaseId));
    }
    @PatchMapping("/{coverageId}/archive") @Operation(summary = "Archive coverage")
    public ApiResponse<TestCaseCoverageResponse> archive(@PathVariable UUID projectId, @PathVariable UUID coverageId) {
        return ApiResponse.success(archive.execute(projectId, coverageId));
    }
}
