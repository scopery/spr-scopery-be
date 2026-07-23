package com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class FunctionalItemKnowledgeSourceAdapter {

    private final FunctionalItemRepository items;

    public FunctionalItemKnowledgeSourceAdapter(FunctionalItemRepository items) {
        this.items = items;
    }

    public Optional<KnowledgeSourceSnapshot> buildSnapshot(UUID projectId, UUID itemId) {
        return items.findByIdAndProjectId(itemId, projectId).map(item -> {
            String normalizedText = buildNormalizedText(item);
            List<String> aclTokens = buildAclTokens(item.workspaceId(), item.projectId());
            return new KnowledgeSourceSnapshot(
                    item.workspaceId(),
                    item.projectId(),
                    KnowledgeSourceType.FUNCTIONAL_ITEM,
                    itemId,
                    stableVersionRef(itemId, item.version()),
                    item.title(),
                    "und",
                    "INTERNAL",
                    normalizedText,
                    Map.of(
                            "code", item.code(),
                            "type", item.type() != null ? item.type().name() : "",
                            "priority", item.priority() != null ? item.priority().name() : "",
                            "status", item.status() != null ? item.status().name() : "",
                            "moduleId", item.moduleId() != null ? item.moduleId().toString() : ""
                    ),
                    aclTokens,
                    String.valueOf(item.version()),
                    "/projects/" + projectId + "/functional-items/" + itemId,
                    item.updatedAt() != null ? item.updatedAt() : item.createdAt()
            );
        });
    }

    private String buildNormalizedText(FunctionalItem item) {
        var sb = new StringBuilder();
        appendSection(sb, "Title", item.title());
        appendSection(sb, "Code", item.code());
        if (item.type() != null) appendSection(sb, "Type", item.type().name());
        if (item.priority() != null) appendSection(sb, "Priority", item.priority().name());
        appendSection(sb, "Description", item.description());
        if (item.acceptanceCriteria() != null && !item.acceptanceCriteria().isEmpty()) {
            appendSection(sb, "Acceptance Criteria", String.join("\n", item.acceptanceCriteria()));
        }
        return sb.toString().strip();
    }

    private void appendSection(StringBuilder sb, String heading, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(heading).append("\n").append(value).append("\n\n");
        }
    }

    private List<String> buildAclTokens(UUID workspaceId, UUID projectId) {
        List<String> tokens = new ArrayList<>();
        tokens.add("workspace:" + workspaceId);
        tokens.add("project:" + projectId);
        tokens.sort(String::compareTo);
        return List.copyOf(tokens);
    }

    private UUID stableVersionRef(UUID itemId, int version) {
        return UUID.nameUUIDFromBytes(("FUNCTIONAL_ITEM:" + itemId + ":" + version).getBytes());
    }
}
