package com.company.scopery.modules.quality.release.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.release.application.action.*;
import com.company.scopery.modules.quality.release.application.command.*;
import com.company.scopery.modules.quality.release.application.response.ReleasePackageResponse;
import com.company.scopery.modules.quality.release.application.service.ReleasePackageQueryService;
import com.company.scopery.modules.quality.release.http.request.CreateReleasePackageRequest;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(QualityApiPaths.RELEASES)
@Tag(name = "Quality - Releases")
public class ReleasePackageController {
    private final CreateReleasePackageAction create;
    private final CheckReleaseReadinessAction checkReadiness;
    private final MarkReadyReleaseAction markReady;
    private final MarkReleasedAction markReleased;
    private final MarkRolledBackReleaseAction markRolledBack;
    private final ArchiveReleasePackageAction archive;
    private final ReleasePackageQueryService query;

    public ReleasePackageController(CreateReleasePackageAction create, CheckReleaseReadinessAction checkReadiness,
                                    MarkReadyReleaseAction markReady, MarkReleasedAction markReleased,
                                    MarkRolledBackReleaseAction markRolledBack, ArchiveReleasePackageAction archive,
                                    ReleasePackageQueryService query) {
        this.create = create;
        this.checkReadiness = checkReadiness;
        this.markReady = markReady;
        this.markReleased = markReleased;
        this.markRolledBack = markRolledBack;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create release package")
    public ApiResponse<ReleasePackageResponse> create(@PathVariable UUID projectId,
                                                      @Valid @RequestBody CreateReleasePackageRequest r) {
        return ApiResponse.success(create.execute(new CreateReleasePackageCommand(
                projectId, r.code(), r.versionLabel(), r.name(), r.description(), r.releaseType(), r.plannedReleaseDate())));
    }

    @GetMapping
    @Operation(summary = "List releases")
    public ApiResponse<List<ReleasePackageResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{releasePackageId}")
    @Operation(summary = "Get release")
    public ApiResponse<ReleasePackageResponse> get(@PathVariable UUID projectId, @PathVariable UUID releasePackageId) {
        return ApiResponse.success(query.get(projectId, releasePackageId));
    }

    @PostMapping("/{releasePackageId}/check-readiness")
    @Operation(summary = "Check release readiness")
    public ApiResponse<ReleasePackageResponse> checkReadiness(@PathVariable UUID projectId,
                                                              @PathVariable UUID releasePackageId) {
        return ApiResponse.success(checkReadiness.execute(new CheckReleaseReadinessCommand(projectId, releasePackageId)));
    }

    @PostMapping("/{releasePackageId}/mark-ready")
    @Operation(summary = "Mark release ready")
    public ApiResponse<ReleasePackageResponse> markReady(@PathVariable UUID projectId,
                                                         @PathVariable UUID releasePackageId) {
        return ApiResponse.success(markReady.execute(new MarkReadyReleaseCommand(projectId, releasePackageId)));
    }

    @PostMapping("/{releasePackageId}/mark-released")
    @Operation(summary = "Mark released")
    public ApiResponse<ReleasePackageResponse> markReleased(@PathVariable UUID projectId,
                                                            @PathVariable UUID releasePackageId) {
        return ApiResponse.success(markReleased.execute(new MarkReleasedCommand(projectId, releasePackageId)));
    }

    @PostMapping("/{releasePackageId}/mark-rolled-back")
    @Operation(summary = "Mark rolled back")
    public ApiResponse<ReleasePackageResponse> markRolledBack(@PathVariable UUID projectId,
                                                              @PathVariable UUID releasePackageId) {
        return ApiResponse.success(markRolledBack.execute(new MarkRolledBackCommand(projectId, releasePackageId)));
    }

    @PatchMapping("/{releasePackageId}/archive")
    @Operation(summary = "Archive release")
    public ApiResponse<ReleasePackageResponse> archive(@PathVariable UUID projectId,
                                                       @PathVariable UUID releasePackageId) {
        return ApiResponse.success(archive.execute(new ArchiveReleasePackageCommand(projectId, releasePackageId)));
    }
}
