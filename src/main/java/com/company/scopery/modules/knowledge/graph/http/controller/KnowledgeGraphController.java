package com.company.scopery.modules.knowledge.graph.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.knowledge.graph.application.response.GraphRelatedResponse;
import com.company.scopery.modules.knowledge.graph.application.service.KnowledgeGraphQueryService;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(KnowledgeApiPaths.GRAPH)
@Tag(name = "Knowledge - Graph")
public class KnowledgeGraphController {

    private final KnowledgeGraphQueryService graphQueryService;

    public KnowledgeGraphController(KnowledgeGraphQueryService graphQueryService) {
        this.graphQueryService = graphQueryService;
    }

    @GetMapping("/nodes/{nodeId}/related")
    @Operation(summary = "Get related graph nodes (BFS, depth ≤ 2, fan-out ≤ 25, ACL-filtered)")
    public ResponseEntity<ApiResponse<GraphRelatedResponse>> getRelated(
            @PathVariable UUID nodeId,
            @RequestHeader(value = "X-Acl-Tokens", required = false) List<String> aclTokens,
            @RequestParam(defaultValue = "1") int depth,
            @RequestParam(defaultValue = "20") int limit) {
        GraphRelatedResponse response = graphQueryService.getRelated(nodeId, aclTokens, depth, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
