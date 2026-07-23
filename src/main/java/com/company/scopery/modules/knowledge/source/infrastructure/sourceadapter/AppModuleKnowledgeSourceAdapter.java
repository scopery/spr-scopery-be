package com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModule;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModuleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class AppModuleKnowledgeSourceAdapter {

    private final RegistryAppModuleRepository modules;

    public AppModuleKnowledgeSourceAdapter(RegistryAppModuleRepository modules) {
        this.modules = modules;
    }

    public Optional<KnowledgeSourceSnapshot> buildSnapshot(UUID workspaceId, UUID moduleId) {
        return modules.findByIdAndWorkspaceId(moduleId, workspaceId).map(module -> {
            String normalizedText = buildNormalizedText(module);
            return new KnowledgeSourceSnapshot(
                    module.workspaceId(),
                    null,
                    KnowledgeSourceType.APP_MODULE,
                    moduleId,
                    stableVersionRef(moduleId, module.version()),
                    module.name(),
                    "und",
                    "INTERNAL",
                    normalizedText,
                    Map.of(
                            "code", module.code(),
                            "status", module.status() != null ? module.status().name() : "",
                            "applicationId", module.applicationId() != null ? module.applicationId().toString() : ""
                    ),
                    List.of("workspace:" + module.workspaceId()),
                    String.valueOf(module.version()),
                    "/workspaces/" + workspaceId + "/modules/" + moduleId,
                    module.updatedAt() != null ? module.updatedAt() : module.createdAt()
            );
        });
    }

    private String buildNormalizedText(RegistryAppModule module) {
        var sb = new StringBuilder();
        appendSection(sb, "Name", module.name());
        appendSection(sb, "Code", module.code());
        appendSection(sb, "Description", module.description());
        return sb.toString().strip();
    }

    private void appendSection(StringBuilder sb, String heading, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(heading).append("\n").append(value).append("\n\n");
        }
    }

    private UUID stableVersionRef(UUID moduleId, int version) {
        return UUID.nameUUIDFromBytes(("APP_MODULE:" + moduleId + ":" + version).getBytes());
    }
}
