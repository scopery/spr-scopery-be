package com.company.scopery.modules.knowledge.retrieval.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.knowledge.retrieval.application.query.SearchKnowledgeQuery;
import com.company.scopery.modules.knowledge.retrieval.application.response.RetrievalResponse;
import com.company.scopery.modules.knowledge.retrieval.application.service.HybridRetrievalService;
import com.company.scopery.modules.knowledge.retrieval.http.request.SearchKnowledgeRequest;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(KnowledgeApiPaths.RETRIEVAL)
@Tag(name = "Knowledge - Retrieval")
public class KnowledgeRetrievalController {

    private final HybridRetrievalService hybridRetrievalService;

    public KnowledgeRetrievalController(HybridRetrievalService hybridRetrievalService) {
        this.hybridRetrievalService = hybridRetrievalService;
    }

    @PostMapping("/search")
    @Operation(summary = "Hybrid BM25+KNN knowledge search with ACL filtering")
    public ResponseEntity<ApiResponse<RetrievalResponse>> search(
            @RequestHeader("X-Workspace-Id") UUID workspaceId,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId,
            @RequestHeader(value = "X-Acl-Tokens", required = false) List<String> aclTokens,
            @Valid @RequestBody SearchKnowledgeRequest request) {
        SearchKnowledgeQuery query = new SearchKnowledgeQuery(
                workspaceId, request.projectId(), actorId, aclTokens, request.query(), request.topK());
        RetrievalResponse response = hybridRetrievalService.search(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
