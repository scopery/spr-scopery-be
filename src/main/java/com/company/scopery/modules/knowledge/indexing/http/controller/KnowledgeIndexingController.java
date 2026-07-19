package com.company.scopery.modules.knowledge.indexing.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.knowledge.indexing.application.action.ReindexProjectAction;
import com.company.scopery.modules.knowledge.indexing.application.action.ReindexWorkspaceAction;
import com.company.scopery.modules.knowledge.indexing.application.command.ReindexProjectCommand;
import com.company.scopery.modules.knowledge.indexing.application.command.ReindexWorkspaceCommand;
import com.company.scopery.modules.knowledge.indexing.application.response.IndexJobResponse;
import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeIndexingQueryService;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(KnowledgeApiPaths.INDEXING)
@Tag(name = "Knowledge - Indexing")
public class KnowledgeIndexingController {

    private final ReindexProjectAction reindexProjectAction;
    private final ReindexWorkspaceAction reindexWorkspaceAction;
    private final KnowledgeIndexingQueryService indexingQueryService;

    public KnowledgeIndexingController(ReindexProjectAction reindexProjectAction,
                                        ReindexWorkspaceAction reindexWorkspaceAction,
                                        KnowledgeIndexingQueryService indexingQueryService) {
        this.reindexProjectAction = reindexProjectAction;
        this.reindexWorkspaceAction = reindexWorkspaceAction;
        this.indexingQueryService = indexingQueryService;
    }

    @PostMapping("/projects/{projectId}/reindex")
    @Operation(summary = "Trigger re-index for all sources in a project")
    public ResponseEntity<ApiResponse<IndexJobResponse>> reindexProject(
            @RequestHeader("X-Workspace-Id") UUID workspaceId,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId,
            @PathVariable UUID projectId) {
        IndexJobResponse response = reindexProjectAction.execute(
                new ReindexProjectCommand(workspaceId, projectId, actorId));
        return ResponseEntity.accepted().body(ApiResponse.success(response));
    }

    @PostMapping("/workspaces/{workspaceId}/reindex")
    @Operation(summary = "Trigger re-index for all sources in a workspace")
    public ResponseEntity<ApiResponse<IndexJobResponse>> reindexWorkspace(
            @PathVariable UUID workspaceId,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {
        IndexJobResponse response = reindexWorkspaceAction.execute(
                new ReindexWorkspaceCommand(workspaceId, actorId));
        return ResponseEntity.accepted().body(ApiResponse.success(response));
    }

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Get index job status")
    public ResponseEntity<ApiResponse<IndexJobResponse>> getJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(indexingQueryService.findJob(jobId)));
    }
}
