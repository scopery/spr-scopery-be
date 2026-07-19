package com.company.scopery.modules.quality.qualityplan.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.qualityplan.application.action.*;
import com.company.scopery.modules.quality.qualityplan.application.command.*;
import com.company.scopery.modules.quality.qualityplan.application.response.QualityPlanResponse;
import com.company.scopery.modules.quality.qualityplan.application.service.QualityPlanQueryService;
import com.company.scopery.modules.quality.qualityplan.http.request.*;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(QualityApiPaths.QUALITY_PLANS)
@Tag(name = "Quality - Quality Plans")
public class QualityPlanController {
    private final CreateQualityPlanAction create;
    private final UpdateQualityPlanAction update;
    private final ApproveQualityPlanAction approve;
    private final MarkCurrentQualityPlanAction markCurrent;
    private final ArchiveQualityPlanAction archive;
    private final QualityPlanQueryService query;
    public QualityPlanController(CreateQualityPlanAction create, UpdateQualityPlanAction update,
                                 ApproveQualityPlanAction approve, MarkCurrentQualityPlanAction markCurrent,
                                 ArchiveQualityPlanAction archive, QualityPlanQueryService query) {
        this.create = create; this.update = update; this.approve = approve;
        this.markCurrent = markCurrent; this.archive = archive; this.query = query;
    }
    @PostMapping @Operation(summary = "Create quality plan")
    public ApiResponse<QualityPlanResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateQualityPlanRequest r) {
        return ApiResponse.success(create.execute(new CreateQualityPlanCommand(projectId, r.code(), r.name(), r.description(),
                r.qualityObjectives(), r.testStrategy(), r.entryCriteria(), r.exitCriteria())));
    }
    @GetMapping @Operation(summary = "List quality plans")
    public ApiResponse<List<QualityPlanResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{qualityPlanId}") @Operation(summary = "Get quality plan")
    public ApiResponse<QualityPlanResponse> get(@PathVariable UUID projectId, @PathVariable UUID qualityPlanId) {
        return ApiResponse.success(query.get(projectId, qualityPlanId));
    }
    @PutMapping("/{qualityPlanId}") @Operation(summary = "Update quality plan")
    public ApiResponse<QualityPlanResponse> update(@PathVariable UUID projectId, @PathVariable UUID qualityPlanId,
                                                   @Valid @RequestBody UpdateQualityPlanRequest r) {
        return ApiResponse.success(update.execute(new UpdateQualityPlanCommand(projectId, qualityPlanId, r.name(), r.description(),
                r.qualityObjectives(), r.testStrategy(), r.entryCriteria(), r.exitCriteria())));
    }
    @PostMapping("/{qualityPlanId}/approve") @Operation(summary = "Approve quality plan")
    public ApiResponse<QualityPlanResponse> approve(@PathVariable UUID projectId, @PathVariable UUID qualityPlanId) {
        return ApiResponse.success(approve.execute(projectId, qualityPlanId));
    }
    @PostMapping("/{qualityPlanId}/mark-current") @Operation(summary = "Mark quality plan current")
    public ApiResponse<QualityPlanResponse> markCurrent(@PathVariable UUID projectId, @PathVariable UUID qualityPlanId) {
        return ApiResponse.success(markCurrent.execute(projectId, qualityPlanId));
    }
    @PatchMapping("/{qualityPlanId}/archive") @Operation(summary = "Archive quality plan")
    public ApiResponse<QualityPlanResponse> archive(@PathVariable UUID projectId, @PathVariable UUID qualityPlanId) {
        return ApiResponse.success(archive.execute(projectId, qualityPlanId));
    }
}
