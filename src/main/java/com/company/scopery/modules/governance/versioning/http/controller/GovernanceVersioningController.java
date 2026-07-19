package com.company.scopery.modules.governance.versioning.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.shared.constant.GovernanceApiPaths;
import com.company.scopery.modules.governance.versioning.application.action.*;
import com.company.scopery.modules.governance.versioning.application.response.*;
import com.company.scopery.modules.governance.versioning.application.service.*;
import com.company.scopery.modules.governance.versioning.http.request.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @RequestMapping(GovernanceApiPaths.PROJECT) @Tag(name = "Governance - Versioning")
public class GovernanceVersioningController {
    private final CreateVersionSnapshotAction createVersion; private final RequestRestoreAction restore;
    private final VersioningQueryService query; private final BaselineGuardService baselineGuard;
    private final ProjectRepository projects; private final GovernanceAuthorizationService authorization;
    public GovernanceVersioningController(CreateVersionSnapshotAction createVersion, RequestRestoreAction restore, VersioningQueryService query,
                                          BaselineGuardService baselineGuard, ProjectRepository projects, GovernanceAuthorizationService authorization) {
        this.createVersion=createVersion; this.restore=restore; this.query=query; this.baselineGuard=baselineGuard;
        this.projects=projects; this.authorization=authorization;
    }
    @PostMapping("/versions") @Operation(summary="Create version snapshot")
    public ApiResponse<GovernanceVersionRecordResponse> createVersion(@PathVariable UUID projectId, @Valid @RequestBody CreateVersionSnapshotRequest r) {
        return ApiResponse.success(createVersion.execute(new com.company.scopery.modules.governance.versioning.application.command.CreateVersionSnapshotCommand(projectId, r.objectTypeCode(), r.targetId(), r.snapshotJson(), r.changeReason())));
    }
    @GetMapping("/versions") @Operation(summary="List versions")
    public ApiResponse<List<GovernanceVersionRecordResponse>> listVersions(@PathVariable UUID projectId, @RequestParam String objectTypeCode, @RequestParam UUID targetId) {
        return ApiResponse.success(query.listVersions(projectId, objectTypeCode, targetId));
    }
    @GetMapping("/snapshots/{snapshotId}") @Operation(summary="Get snapshot")
    public ApiResponse<GovernanceSnapshotResponse> getSnapshot(@PathVariable UUID projectId, @PathVariable UUID snapshotId) {
        return ApiResponse.success(query.getSnapshot(projectId, snapshotId));
    }
    @GetMapping("/snapshots/diff") @Operation(summary="Diff snapshots")
    public ApiResponse<SnapshotDiffResponse> diff(@PathVariable UUID projectId, @RequestParam UUID leftSnapshotId, @RequestParam UUID rightSnapshotId) {
        return ApiResponse.success(query.diff(projectId, leftSnapshotId, rightSnapshotId));
    }
    @PostMapping("/restore") @Operation(summary="Request restore from version")
    public ApiResponse<RestoreRequestResponse> restore(@PathVariable UUID projectId, @Valid @RequestBody CreateRestoreRequest r) {
        return ApiResponse.success(restore.execute(new com.company.scopery.modules.governance.versioning.application.command.RequestRestoreCommand(projectId, r.objectTypeCode(), r.targetId(), r.restoreFromVersionRecordId(), r.reason())));
    }
    @PostMapping("/baseline-guard/check") @Operation(summary="Check baseline guard")
    public ApiResponse<Map<String, Object>> check(@PathVariable UUID projectId, @Valid @RequestBody BaselineGuardCheckRequest r) {
        authorization.requireVersionView(projectId);
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        baselineGuard.assertChangeAllowed(project.workspaceId(), r.objectTypeCode(), r.targetId(), r.proposedChangeType());
        return ApiResponse.success(Map.of("allowed", true));
    }
}
