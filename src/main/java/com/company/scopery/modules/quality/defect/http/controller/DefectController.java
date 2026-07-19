package com.company.scopery.modules.quality.defect.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.defect.application.action.*;
import com.company.scopery.modules.quality.defect.application.command.*;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.application.service.DefectQueryService;
import com.company.scopery.modules.quality.defect.http.request.*;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(QualityApiPaths.DEFECTS)
@Tag(name = "Quality - Defects")
public class DefectController {
    private final CreateDefectAction create;
    private final UpdateDefectAction update;
    private final TriageDefectAction triage;
    private final AssignDefectAction assign;
    private final MarkFixedDefectAction markFixed;
    private final ReadyForRetestDefectAction readyForRetest;
    private final VerifyDefectAction verify;
    private final CloseDefectAction close;
    private final ReopenDefectAction reopen;
    private final ArchiveDefectAction archive;
    private final DefectQueryService query;

    public DefectController(CreateDefectAction create, UpdateDefectAction update, TriageDefectAction triage,
                            AssignDefectAction assign, MarkFixedDefectAction markFixed,
                            ReadyForRetestDefectAction readyForRetest, VerifyDefectAction verify,
                            CloseDefectAction close, ReopenDefectAction reopen, ArchiveDefectAction archive,
                            DefectQueryService query) {
        this.create = create;
        this.update = update;
        this.triage = triage;
        this.assign = assign;
        this.markFixed = markFixed;
        this.readyForRetest = readyForRetest;
        this.verify = verify;
        this.close = close;
        this.reopen = reopen;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create defect")
    public ApiResponse<DefectResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateDefectRequest r) {
        return ApiResponse.success(create.execute(new CreateDefectCommand(projectId, r.code(), r.title(), r.description(),
                r.category(), r.severity(), r.priority(), r.reproductionSteps(), r.expectedResult(), r.actualResult(),
                r.sourceTestCaseResultId())));
    }

    @GetMapping
    @Operation(summary = "List defects")
    public ApiResponse<List<DefectResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{defectId}")
    @Operation(summary = "Get defect")
    public ApiResponse<DefectResponse> get(@PathVariable UUID projectId, @PathVariable UUID defectId) {
        return ApiResponse.success(query.get(projectId, defectId));
    }

    @PutMapping("/{defectId}")
    @Operation(summary = "Update defect")
    public ApiResponse<DefectResponse> update(@PathVariable UUID projectId, @PathVariable UUID defectId,
                                              @Valid @RequestBody UpdateDefectRequest r) {
        return ApiResponse.success(update.execute(new UpdateDefectCommand(
                projectId, defectId, r.title(), r.description(), r.category(), r.severity(),
                r.priority(), r.reproductionSteps(), r.expectedResult(), r.actualResult(), r.environmentNotes())));
    }

    @PostMapping("/{defectId}/triage")
    @Operation(summary = "Triage defect")
    public ApiResponse<DefectResponse> triage(@PathVariable UUID projectId, @PathVariable UUID defectId) {
        return ApiResponse.success(triage.execute(projectId, defectId));
    }

    @PostMapping("/{defectId}/assign")
    @Operation(summary = "Assign defect")
    public ApiResponse<DefectResponse> assign(@PathVariable UUID projectId, @PathVariable UUID defectId,
                                              @Valid @RequestBody AssignDefectRequest r) {
        return ApiResponse.success(assign.execute(new AssignDefectCommand(projectId, defectId, r.assignedToUserId())));
    }

    @PostMapping("/{defectId}/mark-fixed")
    @Operation(summary = "Mark defect fixed")
    public ApiResponse<DefectResponse> markFixed(@PathVariable UUID projectId, @PathVariable UUID defectId) {
        return ApiResponse.success(markFixed.execute(projectId, defectId));
    }

    @PostMapping("/{defectId}/ready-for-retest")
    @Operation(summary = "Mark ready for retest")
    public ApiResponse<DefectResponse> readyForRetest(@PathVariable UUID projectId, @PathVariable UUID defectId) {
        return ApiResponse.success(readyForRetest.execute(projectId, defectId));
    }

    @PostMapping("/{defectId}/verify")
    @Operation(summary = "Verify defect")
    public ApiResponse<DefectResponse> verify(@PathVariable UUID projectId, @PathVariable UUID defectId) {
        return ApiResponse.success(verify.execute(projectId, defectId));
    }

    @PostMapping("/{defectId}/close")
    @Operation(summary = "Close defect")
    public ApiResponse<DefectResponse> close(@PathVariable UUID projectId, @PathVariable UUID defectId,
                                             @Valid @RequestBody CloseDefectRequest r) {
        return ApiResponse.success(close.execute(
                new CloseDefectCommand(projectId, defectId, r.resolutionType(), r.resolutionNote())));
    }

    @PostMapping("/{defectId}/reopen")
    @Operation(summary = "Reopen defect")
    public ApiResponse<DefectResponse> reopen(@PathVariable UUID projectId, @PathVariable UUID defectId,
                                              @Valid @RequestBody ReopenDefectRequest r) {
        return ApiResponse.success(reopen.execute(new ReopenDefectCommand(projectId, defectId, r.reason())));
    }

    @PatchMapping("/{defectId}/archive")
    @Operation(summary = "Archive defect")
    public ApiResponse<DefectResponse> archive(@PathVariable UUID projectId, @PathVariable UUID defectId) {
        return ApiResponse.success(archive.execute(projectId, defectId));
    }
}
