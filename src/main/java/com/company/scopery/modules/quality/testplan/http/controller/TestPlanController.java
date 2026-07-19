package com.company.scopery.modules.quality.testplan.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import com.company.scopery.modules.quality.testplan.application.action.*;
import com.company.scopery.modules.quality.testplan.application.command.CreateTestPlanCommand;
import com.company.scopery.modules.quality.testplan.application.response.TestPlanResponse;
import com.company.scopery.modules.quality.testplan.application.service.TestPlanQueryService;
import com.company.scopery.modules.quality.testplan.http.request.CreateTestPlanRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(QualityApiPaths.TEST_PLANS) @Tag(name="Quality - Test Plans")
public class TestPlanController {
    private final CreateTestPlanAction create; private final ApproveTestPlanAction approve;
    private final ArchiveTestPlanAction archive; private final TestPlanQueryService query;
    public TestPlanController(CreateTestPlanAction create, ApproveTestPlanAction approve, ArchiveTestPlanAction archive, TestPlanQueryService query) {
        this.create=create; this.approve=approve; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary="Create test plan")
    public ApiResponse<TestPlanResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateTestPlanRequest r) {
        return ApiResponse.success(create.execute(new CreateTestPlanCommand(projectId, r.code(), r.name(), r.description(), r.testLevel(), r.qualityPlanId(), r.releasePackageId())));
    }
    @GetMapping @Operation(summary="List test plans")
    public ApiResponse<List<TestPlanResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{testPlanId}") @Operation(summary="Get test plan")
    public ApiResponse<TestPlanResponse> get(@PathVariable UUID projectId, @PathVariable UUID testPlanId) { return ApiResponse.success(query.get(projectId, testPlanId)); }
    @PostMapping("/{testPlanId}/approve") @Operation(summary="Approve test plan")
    public ApiResponse<TestPlanResponse> approve(@PathVariable UUID projectId, @PathVariable UUID testPlanId) { return ApiResponse.success(approve.execute(projectId, testPlanId)); }
    @PatchMapping("/{testPlanId}/archive") @Operation(summary="Archive test plan")
    public ApiResponse<TestPlanResponse> archive(@PathVariable UUID projectId, @PathVariable UUID testPlanId) { return ApiResponse.success(archive.execute(projectId, testPlanId)); }
}
