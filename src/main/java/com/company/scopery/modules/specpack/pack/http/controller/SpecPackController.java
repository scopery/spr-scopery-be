package com.company.scopery.modules.specpack.pack.http.controller;

import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.specpack.pack.application.action.ArchiveSpecPackAction;
import com.company.scopery.modules.specpack.pack.application.action.CreateSpecPackAction;
import com.company.scopery.modules.specpack.pack.application.action.RestoreSpecPackAction;
import com.company.scopery.modules.specpack.pack.application.action.UpdateSpecPackAction;
import com.company.scopery.modules.specpack.pack.application.command.CreateSpecPackCommand;
import com.company.scopery.modules.specpack.pack.application.command.UpdateSpecPackCommand;
import com.company.scopery.modules.specpack.pack.application.query.SearchSpecPackQuery;
import com.company.scopery.modules.specpack.pack.application.response.SpecPackResponse;
import com.company.scopery.modules.specpack.pack.application.service.SpecPackQueryService;
import com.company.scopery.modules.specpack.pack.http.request.CreateSpecPackRequest;
import com.company.scopery.modules.specpack.pack.http.request.SearchSpecPackRequest;
import com.company.scopery.modules.specpack.pack.http.request.UpdateSpecPackRequest;
import com.company.scopery.modules.specpack.shared.constant.SpecPackApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Spec Pack - Packs")
@RestController
@RequestMapping(SpecPackApiPaths.SPEC_PACKS)
public class SpecPackController {

    private final CreateSpecPackAction createAction;
    private final UpdateSpecPackAction updateAction;
    private final ArchiveSpecPackAction archiveAction;
    private final RestoreSpecPackAction restoreAction;
    private final SpecPackQueryService queryService;

    public SpecPackController(CreateSpecPackAction createAction,
                              UpdateSpecPackAction updateAction,
                              ArchiveSpecPackAction archiveAction,
                              RestoreSpecPackAction restoreAction,
                              SpecPackQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.archiveAction = archiveAction;
        this.restoreAction = restoreAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new Spec Pack")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SpecPackResponse> create(@PathVariable UUID projectId,
                                                @Valid @RequestBody CreateSpecPackRequest request) {
        return ApiResponse.success(createAction.execute(new CreateSpecPackCommand(
                projectId, request.packType(), request.name(), request.description(), request.sourcePackId()
        )));
    }

    @Operation(summary = "Get Spec Pack by ID")
    @GetMapping("/{packId}")
    public ApiResponse<SpecPackResponse> getById(@PathVariable UUID projectId,
                                                  @PathVariable UUID packId) {
        return ApiResponse.success(queryService.getById(projectId, packId));
    }

    @Operation(summary = "Search Spec Packs in project")
    @GetMapping
    public ApiResponse<PageResult<SpecPackResponse>> search(@PathVariable UUID projectId,
                                                             @ModelAttribute SearchSpecPackRequest request) {
        return ApiResponse.success(queryService.search(new SearchSpecPackQuery(
                projectId, request.keyword(), request.packType(), request.status(),
                request.page(), request.size(), request.sortBy(), request.ascending()
        )));
    }

    @Operation(summary = "Update Spec Pack")
    @PutMapping("/{packId}")
    public ApiResponse<SpecPackResponse> update(@PathVariable UUID projectId,
                                                 @PathVariable UUID packId,
                                                 @Valid @RequestBody UpdateSpecPackRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateSpecPackCommand(
                projectId, packId, request.name(), request.description()
        )));
    }

    @Operation(summary = "Archive Spec Pack")
    @DeleteMapping("/{packId}")
    public ApiResponse<SpecPackResponse> archive(@PathVariable UUID projectId,
                                                  @PathVariable UUID packId) {
        return ApiResponse.success(archiveAction.execute(projectId, packId));
    }

    @Operation(summary = "Restore archived Spec Pack")
    @PostMapping("/{packId}/restore")
    public ApiResponse<SpecPackResponse> restore(@PathVariable UUID projectId,
                                                  @PathVariable UUID packId) {
        return ApiResponse.success(restoreAction.execute(projectId, packId));
    }
}
