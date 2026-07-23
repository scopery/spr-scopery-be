package com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItem;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItemRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class NonFunctionalItemKnowledgeSourceAdapter {

    private final NonFunctionalItemRepository items;

    public NonFunctionalItemKnowledgeSourceAdapter(NonFunctionalItemRepository items) {
        this.items = items;
    }

    public Optional<KnowledgeSourceSnapshot> buildSnapshot(UUID projectId, UUID itemId) {
        return items.findByIdAndProjectId(itemId, projectId).map(item -> {
            String normalizedText = buildNormalizedText(item);
            List<String> aclTokens = buildAclTokens(item.workspaceId(), item.projectId());
            return new KnowledgeSourceSnapshot(
                    item.workspaceId(),
                    item.projectId(),
                    KnowledgeSourceType.NON_FUNCTIONAL_ITEM,
                    itemId,
                    stableVersionRef(itemId, item.version()),
                    item.title(),
                    "und",
                    "INTERNAL",
                    normalizedText,
                    Map.of(
                            "code", item.code(),
                            "category", item.category() != null ? item.category().name() : "",
                            "priority", item.priority() != null ? item.priority().name() : "",
                            "status", item.status() != null ? item.status().name() : ""
                    ),
                    aclTokens,
                    String.valueOf(item.version()),
                    "/projects/" + projectId + "/non-functional-items/" + itemId,
                    item.updatedAt() != null ? item.updatedAt() : item.createdAt()
            );
        });
    }

    private String buildNormalizedText(NonFunctionalItem item) {
        var sb = new StringBuilder();
        appendSection(sb, "Title", item.title());
        appendSection(sb, "Code", item.code());
        if (item.category() != null) appendSection(sb, "Category", item.category().name());
        if (item.priority() != null) appendSection(sb, "Priority", item.priority().name());
        appendSection(sb, "Description", item.description());
        appendSection(sb, "Target Metric", item.targetMetric());
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
        return UUID.nameUUIDFromBytes(("NON_FUNCTIONAL_ITEM:" + itemId + ":" + version).getBytes());
    }
}
