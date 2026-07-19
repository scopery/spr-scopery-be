package com.company.scopery.modules.quality.testcase.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import com.company.scopery.modules.quality.testcase.application.action.*; import com.company.scopery.modules.quality.testcase.application.command.CreateTestCaseCommand;
import com.company.scopery.modules.quality.testcase.application.response.TestCaseResponse; import com.company.scopery.modules.quality.testcase.application.service.TestCaseQueryService;
import com.company.scopery.modules.quality.testcase.http.request.CreateTestCaseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(QualityApiPaths.TEST_CASES) @Tag(name="Quality - Test Cases")
public class TestCaseController {
    private final CreateTestCaseAction create; private final ApproveTestCaseAction approve;
    private final ArchiveTestCaseAction archive; private final TestCaseQueryService query;
    public TestCaseController(CreateTestCaseAction create, ApproveTestCaseAction approve, ArchiveTestCaseAction archive, TestCaseQueryService query) {
        this.create=create; this.approve=approve; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary="Create test case")
    public ApiResponse<TestCaseResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateTestCaseRequest r) {
        return ApiResponse.success(create.execute(new CreateTestCaseCommand(projectId, r.testSuiteId(), r.code(), r.title(), r.description(), r.type(), r.priority(), r.preconditions(), r.expectedResult())));
    }
    @GetMapping @Operation(summary="List test cases")
    public ApiResponse<List<TestCaseResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{testCaseId}") @Operation(summary="Get test case")
    public ApiResponse<TestCaseResponse> get(@PathVariable UUID projectId, @PathVariable UUID testCaseId) { return ApiResponse.success(query.get(projectId, testCaseId)); }
    @PostMapping("/{testCaseId}/approve") @Operation(summary="Approve test case")
    public ApiResponse<TestCaseResponse> approve(@PathVariable UUID projectId, @PathVariable UUID testCaseId) { return ApiResponse.success(approve.execute(projectId, testCaseId)); }
    @PatchMapping("/{testCaseId}/archive") @Operation(summary="Archive test case")
    public ApiResponse<TestCaseResponse> archive(@PathVariable UUID projectId, @PathVariable UUID testCaseId) { return ApiResponse.success(archive.execute(projectId, testCaseId)); }
}
