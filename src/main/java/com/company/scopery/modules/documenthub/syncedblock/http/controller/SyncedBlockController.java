package com.company.scopery.modules.documenthub.syncedblock.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import com.company.scopery.modules.documenthub.syncedblock.application.action.ArchiveSyncedBlockAction;
import com.company.scopery.modules.documenthub.syncedblock.application.action.CreateSyncedBlockAction;
import com.company.scopery.modules.documenthub.syncedblock.application.action.UpdateSyncedBlockAction;
import com.company.scopery.modules.documenthub.syncedblock.application.command.CreateSyncedBlockCommand;
import com.company.scopery.modules.documenthub.syncedblock.application.command.UpdateSyncedBlockCommand;
import com.company.scopery.modules.documenthub.syncedblock.application.response.SyncedBlockResponse;
import com.company.scopery.modules.documenthub.syncedblock.application.service.SyncedBlockQueryService;
import com.company.scopery.modules.documenthub.syncedblock.http.request.CreateSyncedBlockRequest;
import com.company.scopery.modules.documenthub.syncedblock.http.request.UpdateSyncedBlockRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Document Hub - Synced Blocks")
public class SyncedBlockController {

    private final CreateSyncedBlockAction createBlock;
    private final UpdateSyncedBlockAction updateBlock;
    private final ArchiveSyncedBlockAction archiveBlock;
    private final SyncedBlockQueryService query;

    public SyncedBlockController(CreateSyncedBlockAction createBlock,
                                  UpdateSyncedBlockAction updateBlock,
                                  ArchiveSyncedBlockAction archiveBlock,
                                  SyncedBlockQueryService query) {
        this.createBlock = createBlock;
        this.updateBlock = updateBlock;
        this.archiveBlock = archiveBlock;
        this.query = query;
    }

    @GetMapping(DocumentHubApiPaths.SYNCED_BLOCKS)
    @Operation(summary = "List synced blocks in a workspace")
    public ApiResponse<List<SyncedBlockResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @GetMapping(DocumentHubApiPaths.SYNCED_BLOCKS + "/{syncedBlockId}")
    @Operation(summary = "Get a synced block")
    public ApiResponse<SyncedBlockResponse> get(@PathVariable UUID workspaceId,
                                                 @PathVariable UUID syncedBlockId) {
        return ApiResponse.success(query.get(workspaceId, syncedBlockId));
    }

    @PostMapping(DocumentHubApiPaths.SYNCED_BLOCKS)
    @Operation(summary = "Create a synced block")
    public ApiResponse<SyncedBlockResponse> create(@PathVariable UUID workspaceId,
                                                    @Valid @RequestBody CreateSyncedBlockRequest r,
                                                    @RequestParam UUID projectId) {
        return ApiResponse.success(createBlock.execute(new CreateSyncedBlockCommand(
                workspaceId, projectId, r.title(), r.ast(), r.schemaVersion())));
    }

    @PutMapping(DocumentHubApiPaths.SYNCED_BLOCKS + "/{syncedBlockId}")
    @Operation(summary = "Update a synced block AST")
    public ApiResponse<SyncedBlockResponse> update(@PathVariable UUID workspaceId,
                                                    @PathVariable UUID syncedBlockId,
                                                    @Valid @RequestBody UpdateSyncedBlockRequest r) {
        return ApiResponse.success(updateBlock.execute(new UpdateSyncedBlockCommand(
                workspaceId, syncedBlockId, r.ast(), r.schemaVersion())));
    }

    @PostMapping(DocumentHubApiPaths.SYNCED_BLOCKS + "/{syncedBlockId}/archive")
    @Operation(summary = "Archive a synced block")
    public ApiResponse<SyncedBlockResponse> archive(@PathVariable UUID workspaceId,
                                                     @PathVariable UUID syncedBlockId) {
        return ApiResponse.success(archiveBlock.execute(workspaceId, syncedBlockId));
    }
}
