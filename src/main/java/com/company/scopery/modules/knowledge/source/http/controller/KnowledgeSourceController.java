package com.company.scopery.modules.knowledge.source.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.knowledge.indexing.application.response.IndexJobResponse;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeApiPaths;
import com.company.scopery.modules.knowledge.source.application.action.ReindexSourceAction;
import com.company.scopery.modules.knowledge.source.application.command.ReindexSourceCommand;
import com.company.scopery.modules.knowledge.source.application.response.KnowledgeChunkResponse;
import com.company.scopery.modules.knowledge.source.application.response.KnowledgeSourceResponse;
import com.company.scopery.modules.knowledge.source.application.service.KnowledgeSourceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(KnowledgeApiPaths.SOURCES)
@Tag(name = "Knowledge - Sources")
public class KnowledgeSourceController {

    private final KnowledgeSourceQueryService sourceQueryService;
    private final ReindexSourceAction reindexSourceAction;

    public KnowledgeSourceController(KnowledgeSourceQueryService sourceQueryService,
                                      ReindexSourceAction reindexSourceAction) {
        this.sourceQueryService = sourceQueryService;
        this.reindexSourceAction = reindexSourceAction;
    }

    @GetMapping("/{sourceId}")
    @Operation(summary = "Get knowledge source metadata")
    public ResponseEntity<ApiResponse<KnowledgeSourceResponse>> findById(@PathVariable UUID sourceId) {
        return ResponseEntity.ok(ApiResponse.success(sourceQueryService.findById(sourceId)));
    }

    @GetMapping("/{sourceId}/chunks")
    @Operation(summary = "List current chunks for a knowledge source")
    public ResponseEntity<ApiResponse<PageResponse<KnowledgeChunkResponse>>> findChunks(
            @PathVariable UUID sourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.success(sourceQueryService.findChunks(sourceId, page, size)));
    }

    @PostMapping("/{sourceId}/reindex")
    @Operation(summary = "Trigger re-index for a single knowledge source")
    public ResponseEntity<ApiResponse<IndexJobResponse>> reindex(
            @PathVariable UUID sourceId,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {
        IndexJobResponse response = reindexSourceAction.execute(new ReindexSourceCommand(sourceId, actorId));
        return ResponseEntity.accepted().body(ApiResponse.success(response));
    }
}
