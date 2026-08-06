package com.company.scopery.modules.specpack.block.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.specpack.block.application.action.CreateBlockAction;
import com.company.scopery.modules.specpack.block.application.action.DeleteBlockAction;
import com.company.scopery.modules.specpack.block.application.action.DuplicateBlockAction;
import com.company.scopery.modules.specpack.block.application.action.ReorderBlocksAction;
import com.company.scopery.modules.specpack.block.application.action.RestoreBlockRevisionAction;
import com.company.scopery.modules.specpack.block.application.action.UpdateBlockAction;
import com.company.scopery.modules.specpack.block.application.command.CreateBlockCommand;
import com.company.scopery.modules.specpack.block.application.command.ReorderBlocksCommand;
import com.company.scopery.modules.specpack.block.application.command.UpdateBlockCommand;
import com.company.scopery.modules.specpack.block.application.response.BlockResponse;
import com.company.scopery.modules.specpack.block.application.response.BlockRevisionResponse;
import com.company.scopery.modules.specpack.block.application.service.SpecPackBlockQueryService;
import com.company.scopery.modules.specpack.block.http.request.CreateBlockRequest;
import com.company.scopery.modules.specpack.block.http.request.ReorderBlocksRequest;
import com.company.scopery.modules.specpack.block.http.request.RestoreBlockRevisionRequest;
import com.company.scopery.modules.specpack.block.http.request.UpdateBlockRequest;
import com.company.scopery.modules.specpack.shared.constant.SpecPackApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Spec Pack - Blocks")
@RestController
@RequestMapping(SpecPackApiPaths.BLOCKS)
public class SpecPackBlockController {

    private final CreateBlockAction createAction;
    private final UpdateBlockAction updateAction;
    private final DeleteBlockAction deleteAction;
    private final ReorderBlocksAction reorderAction;
    private final DuplicateBlockAction duplicateAction;
    private final RestoreBlockRevisionAction restoreRevisionAction;
    private final SpecPackBlockQueryService queryService;

    public SpecPackBlockController(CreateBlockAction createAction,
                                   UpdateBlockAction updateAction,
                                   DeleteBlockAction deleteAction,
                                   ReorderBlocksAction reorderAction,
                                   DuplicateBlockAction duplicateAction,
                                   RestoreBlockRevisionAction restoreRevisionAction,
                                   SpecPackBlockQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
        this.reorderAction = reorderAction;
        this.duplicateAction = duplicateAction;
        this.restoreRevisionAction = restoreRevisionAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new block in Spec Pack")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BlockResponse> create(@PathVariable UUID projectId,
                                              @PathVariable UUID packId,
                                              @Valid @RequestBody CreateBlockRequest request) {
        return ApiResponse.success(createAction.execute(new CreateBlockCommand(
                projectId, packId,
                request.blockKey(), request.parentBlockId(), request.blockType(),
                request.title(), request.contentFormat(),
                request.contentJson(), request.sourceRefsJson(),
                request.displayOrder()
        )));
    }

    @Operation(summary = "Get block by ID")
    @GetMapping("/{blockId}")
    public ApiResponse<BlockResponse> getById(@PathVariable UUID projectId,
                                               @PathVariable UUID packId,
                                               @PathVariable UUID blockId) {
        return ApiResponse.success(queryService.getById(packId, blockId));
    }

    @Operation(summary = "List all blocks in Spec Pack")
    @GetMapping
    public ApiResponse<List<BlockResponse>> listByPack(@PathVariable UUID projectId,
                                                        @PathVariable UUID packId) {
        return ApiResponse.success(queryService.listByPack(packId));
    }

    @Operation(summary = "Update block")
    @PutMapping("/{blockId}")
    public ApiResponse<BlockResponse> update(@PathVariable UUID projectId,
                                              @PathVariable UUID packId,
                                              @PathVariable UUID blockId,
                                              @Valid @RequestBody UpdateBlockRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateBlockCommand(
                projectId, packId, blockId,
                request.parentBlockId(), request.title(), request.contentFormat(),
                request.contentJson(), request.sourceRefsJson(),
                request.expectedRevisionNumber()
        )));
    }

    @Operation(summary = "Delete (soft-delete) block")
    @DeleteMapping("/{blockId}")
    public ApiResponse<Void> delete(@PathVariable UUID projectId,
                                     @PathVariable UUID packId,
                                     @PathVariable UUID blockId) {
        deleteAction.execute(projectId, packId, blockId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "Reorder blocks in Spec Pack")
    @PostMapping("/reorder")
    public ApiResponse<Void> reorder(@PathVariable UUID projectId,
                                      @PathVariable UUID packId,
                                      @Valid @RequestBody ReorderBlocksRequest request) {
        List<ReorderBlocksCommand.BlockOrderItem> items = request.orderedItems().stream()
                .map(i -> new ReorderBlocksCommand.BlockOrderItem(i.blockId(), i.displayOrder()))
                .collect(Collectors.toList());
        reorderAction.execute(new ReorderBlocksCommand(projectId, packId, items));
        return ApiResponse.success(null);
    }

    @Operation(summary = "Duplicate a block")
    @PostMapping("/{blockId}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BlockResponse> duplicate(@PathVariable UUID projectId,
                                                 @PathVariable UUID packId,
                                                 @PathVariable UUID blockId) {
        return ApiResponse.success(duplicateAction.execute(projectId, packId, blockId));
    }

    @Operation(summary = "List revisions of a block")
    @GetMapping("/{blockId}/revisions")
    public ApiResponse<List<BlockRevisionResponse>> listRevisions(@PathVariable UUID projectId,
                                                                   @PathVariable UUID packId,
                                                                   @PathVariable UUID blockId) {
        return ApiResponse.success(queryService.listRevisions(packId, blockId));
    }

    @Operation(summary = "Restore block to a specific revision")
    @PostMapping("/{blockId}/restore-revision")
    public ApiResponse<BlockResponse> restoreRevision(@PathVariable UUID projectId,
                                                       @PathVariable UUID packId,
                                                       @PathVariable UUID blockId,
                                                       @Valid @RequestBody RestoreBlockRevisionRequest request) {
        return ApiResponse.success(restoreRevisionAction.execute(projectId, packId, blockId, request.revisionNumber()));
    }
}
