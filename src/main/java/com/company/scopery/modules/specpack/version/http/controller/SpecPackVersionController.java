package com.company.scopery.modules.specpack.version.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.specpack.shared.constant.SpecPackApiPaths;
import com.company.scopery.modules.specpack.version.application.action.CreatePackVersionAction;
import com.company.scopery.modules.specpack.version.application.action.RestorePackVersionAction;
import com.company.scopery.modules.specpack.version.application.command.CreatePackVersionCommand;
import com.company.scopery.modules.specpack.version.application.command.RestorePackVersionCommand;
import com.company.scopery.modules.specpack.version.application.response.SpecPackVersionResponse;
import com.company.scopery.modules.specpack.version.application.service.SpecPackVersionQueryService;
import com.company.scopery.modules.specpack.version.http.request.CreatePackVersionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Spec Pack - Versions")
@RestController
@RequestMapping(SpecPackApiPaths.VERSIONS)
public class SpecPackVersionController {

    private final CreatePackVersionAction createAction;
    private final RestorePackVersionAction restoreAction;
    private final SpecPackVersionQueryService queryService;

    public SpecPackVersionController(CreatePackVersionAction createAction,
                                     RestorePackVersionAction restoreAction,
                                     SpecPackVersionQueryService queryService) {
        this.createAction = createAction;
        this.restoreAction = restoreAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a version snapshot of the Spec Pack")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SpecPackVersionResponse> create(@PathVariable UUID projectId,
                                                        @PathVariable UUID packId,
                                                        @RequestBody(required = false) CreatePackVersionRequest request) {
        String changeReason = request != null ? request.changeReason() : null;
        return ApiResponse.success(createAction.execute(new CreatePackVersionCommand(projectId, packId, changeReason)));
    }

    @Operation(summary = "List all versions of a Spec Pack")
    @GetMapping
    public ApiResponse<List<SpecPackVersionResponse>> list(@PathVariable UUID projectId,
                                                            @PathVariable UUID packId) {
        return ApiResponse.success(queryService.listByPack(packId));
    }

    @Operation(summary = "Get a specific version by ID")
    @GetMapping("/{versionId}")
    public ApiResponse<SpecPackVersionResponse> getById(@PathVariable UUID projectId,
                                                         @PathVariable UUID packId,
                                                         @PathVariable UUID versionId) {
        return ApiResponse.success(queryService.getById(packId, versionId));
    }

    @Operation(summary = "Restore Spec Pack to a specific version")
    @PostMapping("/{versionId}/restore")
    public ApiResponse<SpecPackVersionResponse> restore(@PathVariable UUID projectId,
                                                         @PathVariable UUID packId,
                                                         @PathVariable UUID versionId) {
        return ApiResponse.success(restoreAction.execute(new RestorePackVersionCommand(projectId, packId, versionId)));
    }
}
