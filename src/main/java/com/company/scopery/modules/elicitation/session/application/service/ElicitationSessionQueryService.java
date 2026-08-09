package com.company.scopery.modules.elicitation.session.application.service;

import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.session.application.response.ElicitationSessionResponse;
import com.company.scopery.modules.elicitation.session.application.response.ScopeLockResponse;
import com.company.scopery.modules.elicitation.session.application.response.ScopeTreeEntityResponse;
import com.company.scopery.modules.elicitation.session.application.response.ScopeTreeResponse;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ElicitationSessionQueryService {

    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationRoundRepository roundRepository;
    private final ElicitationSuggestionRepository suggestionRepository;
    private final ObjectMapper objectMapper;

    public ElicitationSessionQueryService(ElicitationSessionRepository sessionRepository,
                                           ElicitationRoundRepository roundRepository,
                                           ElicitationSuggestionRepository suggestionRepository,
                                           ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.roundRepository = roundRepository;
        this.suggestionRepository = suggestionRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public ElicitationSessionResponse getById(UUID projectId, UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .filter(s -> s.projectId().equals(projectId))
                .map(ElicitationSessionResponse::from)
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(sessionId));
    }

    @Transactional(readOnly = true)
    public List<ElicitationSessionResponse> listByProject(UUID projectId) {
        return sessionRepository.findAllByProjectId(projectId)
                .stream()
                .map(ElicitationSessionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScopeLockResponse checkActiveLock(UUID projectId, UUID scopePackageId) {
        Optional<ElicitationSession> active = sessionRepository
                .findActiveByProjectIdAndScopePackageId(projectId, scopePackageId);
        return active
                .map(s -> new ScopeLockResponse(true, s.id()))
                .orElse(new ScopeLockResponse(false, null));
    }

    @Transactional(readOnly = true)
    public ScopeTreeResponse getScopeTree(UUID projectId, UUID sessionId) {
        sessionRepository.findById(sessionId)
                .filter(s -> s.projectId().equals(projectId))
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(sessionId));

        List<ScopeTreeEntityResponse> before = buildBeforeTree(sessionId);
        List<ScopeTreeEntityResponse> after = buildAfterTree(sessionId, before);
        return new ScopeTreeResponse(before, after);
    }

    private List<ScopeTreeEntityResponse> buildBeforeTree(UUID sessionId) {
        return roundRepository.findFirstBySessionIdOrderByRoundNumberAsc(sessionId)
                .map(round -> parseSnapshotToTree(round.scopeSnapshotJson()))
                .orElse(List.of());
    }

    private List<ScopeTreeEntityResponse> buildAfterTree(UUID sessionId, List<ScopeTreeEntityResponse> before) {
        // Start with a mutable copy of before-nodes indexed by id
        Map<String, ScopeTreeEntityResponse> nodeMap = new HashMap<>();
        for (ScopeTreeEntityResponse node : before) {
            nodeMap.put(node.id(), node);
        }

        // Find all rounds for this session → get suggestions with pending items
        List<ElicitationSuggestionItem> pendingItems = new ArrayList<>();
        List<ElicitationRound> rounds = roundRepository.findAllBySessionId(sessionId);
        for (ElicitationRound round : rounds) {
            suggestionRepository.findByRoundId(round.id()).ifPresent(suggestion -> {
                List<ElicitationSuggestionItem> items = suggestionRepository.findAllItemsBySuggestionId(suggestion.id());
                items.stream()
                        .filter(i -> i.status() == SuggestionItemStatus.PENDING
                                || i.status() == SuggestionItemStatus.APPROVED)
                        .forEach(pendingItems::add);
            });
        }

        // Build after list: start from before, apply pending suggestion overlays
        List<ScopeTreeEntityResponse> afterList = new ArrayList<>();
        for (ScopeTreeEntityResponse node : before) {
            ScopeTreeEntityResponse merged = mergeWithSuggestions(node, pendingItems);
            afterList.add(merged);
        }

        // Add CREATE nodes (entities that don't exist yet in before)
        for (ElicitationSuggestionItem item : pendingItems) {
            String action = item.action();
            if (action != null && action.startsWith("CREATE_")) {
                String entityType = deriveEntityType(action);
                String entityId = item.targetEntityId() != null ? item.targetEntityId().toString()
                        : "pending-" + item.id().toString();
                if (!nodeMap.containsKey(entityId)) {
                    afterList.add(ScopeTreeEntityResponse.withAction(
                            entityId, item.targetEntityName() != null ? item.targetEntityName() : "New " + entityType,
                            entityType, "CREATE"));
                }
            }
        }

        return afterList;
    }

    private ScopeTreeEntityResponse mergeWithSuggestions(ScopeTreeEntityResponse node,
                                                          List<ElicitationSuggestionItem> items) {
        for (ElicitationSuggestionItem item : items) {
            String targetId = item.targetEntityId() != null ? item.targetEntityId().toString() : null;
            if (targetId == null || !targetId.equals(node.id())) continue;
            String action = item.action();
            if (action == null) continue;
            if (action.startsWith("DELETE_")) {
                return new ScopeTreeEntityResponse(node.id(), node.title(), node.type(),
                        node.children(), "DELETE", null);
            }
            if (action.startsWith("UPDATE_")) {
                Map<String, ScopeTreeEntityResponse.FieldChange> changes = parseChanges(item.changesJson());
                return new ScopeTreeEntityResponse(node.id(), node.title(), node.type(),
                        node.children(), "UPDATE", changes.isEmpty() ? null : changes);
            }
        }
        return node;
    }

    private Map<String, ScopeTreeEntityResponse.FieldChange> parseChanges(String changesJson) {
        Map<String, ScopeTreeEntityResponse.FieldChange> result = new HashMap<>();
        if (changesJson == null || changesJson.isBlank()) return result;
        try {
            JsonNode root = objectMapper.readTree(changesJson);
            root.fields().forEachRemaining(entry -> {
                JsonNode val = entry.getValue();
                Object before = val.has("before") ? val.get("before").asText() : null;
                Object after = val.has("after") ? val.get("after").asText() : null;
                result.put(entry.getKey(), new ScopeTreeEntityResponse.FieldChange(before, after));
            });
        } catch (Exception ignored) {}
        return result;
    }

    private List<ScopeTreeEntityResponse> parseSnapshotToTree(String snapshotJson) {
        List<ScopeTreeEntityResponse> nodes = new ArrayList<>();
        if (snapshotJson == null || snapshotJson.isBlank()) return nodes;
        try {
            JsonNode root = objectMapper.readTree(snapshotJson);
            addEntityNodes(nodes, root.path("requirements"), "REQUIREMENT", "title");
            addEntityNodes(nodes, root.path("functions"), "FUNCTION", "title");
            addEntityNodes(nodes, root.path("useCases"), "USE_CASE", "name");
            addEntityNodes(nodes, root.path("screens"), "SCREEN", "name");
            addEntityNodes(nodes, root.path("components"), "COMPONENT", "name");
        } catch (Exception ignored) {}
        return nodes;
    }

    private void addEntityNodes(List<ScopeTreeEntityResponse> nodes, JsonNode array,
                                 String type, String titleField) {
        if (!array.isArray()) return;
        for (JsonNode item : array) {
            String id = item.path("id").asText(null);
            String title = item.path(titleField).asText(null);
            if (id == null || title == null) continue;
            nodes.add(ScopeTreeEntityResponse.simple(id, title, type));
        }
    }

    private static String deriveEntityType(String action) {
        if (action == null) return "UNKNOWN";
        if (action.contains("REQUIREMENT")) return "REQUIREMENT";
        if (action.contains("FUNCTION")) return "FUNCTION";
        if (action.contains("USE_CASE")) return "USE_CASE";
        if (action.contains("SCREEN")) return "SCREEN";
        if (action.contains("COMPONENT")) return "COMPONENT";
        if (action.contains("NOTIFICATION")) return "NOTIFICATION";
        return "UNKNOWN";
    }
}
