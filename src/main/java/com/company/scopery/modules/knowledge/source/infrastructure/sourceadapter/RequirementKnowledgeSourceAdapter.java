package com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import com.company.scopery.modules.traceability.requirement.domain.model.Requirement;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class RequirementKnowledgeSourceAdapter {

    private final RequirementRepository requirements;

    public RequirementKnowledgeSourceAdapter(RequirementRepository requirements) {
        this.requirements = requirements;
    }

    public Optional<KnowledgeSourceSnapshot> buildSnapshot(UUID projectId, UUID requirementId) {
        return requirements.findByIdAndProjectId(requirementId, projectId).map(req -> {
            String normalizedText = buildNormalizedText(req);
            List<String> aclTokens = buildAclTokens(req.workspaceId(), req.projectId());
            int version = req.version() != null ? req.version() : 0;
            return new KnowledgeSourceSnapshot(
                    req.workspaceId(),
                    req.projectId(),
                    KnowledgeSourceType.REQUIREMENT,
                    requirementId,
                    stableVersionRef(requirementId, version),
                    req.title(),
                    "und",
                    "INTERNAL",
                    normalizedText,
                    Map.of(
                            "code", req.code(),
                            "type", req.requirementType() != null ? req.requirementType().name() : "",
                            "priority", req.priority() != null ? req.priority().name() : "",
                            "status", req.status() != null ? req.status().name() : ""
                    ),
                    aclTokens,
                    String.valueOf(version),
                    "/projects/" + projectId + "/requirements/" + requirementId,
                    req.updatedAt() != null ? req.updatedAt() : req.createdAt()
            );
        });
    }

    private String buildNormalizedText(Requirement req) {
        var sb = new StringBuilder();
        appendSection(sb, "Title", req.title());
        appendSection(sb, "Code", req.code());
        if (req.requirementType() != null) appendSection(sb, "Type", req.requirementType().name());
        if (req.priority() != null) appendSection(sb, "Priority", req.priority().name());
        appendSection(sb, "Description", req.description());
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

    private UUID stableVersionRef(UUID reqId, int version) {
        return UUID.nameUUIDFromBytes(("REQUIREMENT:" + reqId + ":" + version).getBytes());
    }
}
