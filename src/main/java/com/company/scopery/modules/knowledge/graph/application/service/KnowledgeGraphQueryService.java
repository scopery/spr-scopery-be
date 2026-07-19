package com.company.scopery.modules.knowledge.graph.application.service;

import com.company.scopery.modules.knowledge.graph.application.response.GraphEdgeResponse;
import com.company.scopery.modules.knowledge.graph.application.response.GraphNodeResponse;
import com.company.scopery.modules.knowledge.graph.application.response.GraphRelatedResponse;
import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeStatus;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphEdge;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphEdgeRepository;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphNode;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphNodeRepository;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class KnowledgeGraphQueryService {

    private static final int MAX_DEPTH = 2;
    private static final int MAX_FAN_OUT = 25;

    private final KnowledgeGraphNodeRepository nodeRepository;
    private final KnowledgeGraphEdgeRepository edgeRepository;

    public KnowledgeGraphQueryService(KnowledgeGraphNodeRepository nodeRepository,
                                       KnowledgeGraphEdgeRepository edgeRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    @Transactional(readOnly = true)
    public GraphRelatedResponse getRelated(UUID nodeId, List<String> callerAclTokens, int depth, int limit) {
        KnowledgeGraphNode root = nodeRepository.findById(nodeId)
                .orElseThrow(() -> KnowledgeExceptions.knowledgeGraphNodeNotFound(nodeId));

        if (root.nodeStatus() != GraphNodeStatus.ACTIVE) {
            throw KnowledgeExceptions.knowledgeGraphNodeNotFound(nodeId);
        }

        int boundedDepth = Math.min(depth, MAX_DEPTH);
        int boundedLimit = Math.min(limit, MAX_FAN_OUT);

        Map<UUID, KnowledgeGraphNode> visitedNodes = new HashMap<>();
        List<KnowledgeGraphEdge> collectedEdges = new ArrayList<>();
        visitedNodes.put(root.id(), root);

        // BFS traversal
        Deque<UUID> queue = new ArrayDeque<>();
        Map<UUID, Integer> depthMap = new HashMap<>();
        queue.add(root.id());
        depthMap.put(root.id(), 0);

        while (!queue.isEmpty() && visitedNodes.size() <= boundedLimit) {
            UUID current = queue.poll();
            int currentDepth = depthMap.getOrDefault(current, 0);
            if (currentDepth >= boundedDepth) continue;

            List<KnowledgeGraphEdge> edges = edgeRepository.findActiveByNodeId(current);
            int fanOut = 0;
            for (KnowledgeGraphEdge edge : edges) {
                if (fanOut >= MAX_FAN_OUT) break;
                UUID neighborId = edge.fromNodeId().equals(current) ? edge.toNodeId() : edge.fromNodeId();
                if (visitedNodes.containsKey(neighborId)) {
                    collectedEdges.add(edge);
                    continue;
                }
                nodeRepository.findById(neighborId).ifPresent(neighbor -> {
                    if (neighbor.nodeStatus() == GraphNodeStatus.ACTIVE
                            && isAccessible(neighbor, callerAclTokens)
                            && visitedNodes.size() <= boundedLimit) {
                        visitedNodes.put(neighborId, neighbor);
                        collectedEdges.add(edge);
                        queue.add(neighborId);
                        depthMap.put(neighborId, currentDepth + 1);
                    }
                });
                fanOut++;
            }
        }

        List<GraphNodeResponse> nodeResponses = visitedNodes.values().stream()
                .map(this::toNodeResponse).toList();

        Set<UUID> nodeIds = visitedNodes.keySet();
        List<GraphEdgeResponse> edgeResponses = collectedEdges.stream()
                .filter(e -> nodeIds.contains(e.fromNodeId()) && nodeIds.contains(e.toNodeId()))
                .distinct()
                .map(this::toEdgeResponse)
                .toList();

        return new GraphRelatedResponse(nodeId, nodeResponses, edgeResponses);
    }

    private boolean isAccessible(KnowledgeGraphNode node, List<String> callerAclTokens) {
        if (callerAclTokens == null || callerAclTokens.isEmpty()) return false;
        if (node.aclTokens() == null || node.aclTokens().isEmpty()) return true;
        Set<String> callerSet = new HashSet<>(callerAclTokens);
        return node.aclTokens().stream().anyMatch(callerSet::contains);
    }

    private GraphNodeResponse toNodeResponse(KnowledgeGraphNode node) {
        return new GraphNodeResponse(node.id(), node.workspaceId(), node.projectId(),
                node.nodeType().name(), node.sourceRefId(), node.sourceVersionRefId(),
                node.title(), node.nodeStatus().name());
    }

    private GraphEdgeResponse toEdgeResponse(KnowledgeGraphEdge edge) {
        return new GraphEdgeResponse(edge.id(), edge.fromNodeId(), edge.toNodeId(),
                edge.edgeType().name(), edge.edgeStatus().name());
    }
}
