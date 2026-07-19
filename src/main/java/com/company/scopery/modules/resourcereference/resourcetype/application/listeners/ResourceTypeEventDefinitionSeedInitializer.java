package com.company.scopery.modules.resourcereference.resourcetype.application.listeners;

import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeDefinition;
import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ResourceTypeEventDefinitionSeedInitializer {

    private static final Logger log = LoggerFactory.getLogger(ResourceTypeEventDefinitionSeedInitializer.class);

    private static final List<String[]> SYSTEM_TYPES = List.of(
            new String[]{"USER", "User"},
            new String[]{"TEAM", "Team"},
            new String[]{"PROJECT", "Project"},
            new String[]{"WORKSPACE", "Workspace"},
            new String[]{"DOCUMENT", "Document"},
            new String[]{"DOCUMENT_VERSION", "Document Version"},
            new String[]{"DOCUMENT_FOLDER", "Document Folder"},
            new String[]{"DOCUMENT_TEMPLATE", "Document Template"},
            new String[]{"REQUIREMENT", "Requirement"},
            new String[]{"TASK", "Task"},
            new String[]{"MILESTONE", "Milestone"},
            new String[]{"ISSUE", "Issue"},
            new String[]{"COMMENT", "Comment"},
            new String[]{"ATTACHMENT", "Attachment"},
            new String[]{"KNOWLEDGE_SOURCE", "Knowledge Source"},
            new String[]{"AI_AGENT", "AI Agent"},
            new String[]{"AI_EXECUTION", "AI Execution"},
            new String[]{"EVENT_DEFINITION", "Event Definition"},
            new String[]{"ORGANIZATION", "Organization"},
            new String[]{"ROLE", "Role"},
            new String[]{"PERMISSION", "Permission"},
            new String[]{"SYNCED_BLOCK", "Synced Block"},
            new String[]{"NATIVE_TEMPLATE", "Native Template"},
            new String[]{"REPORT", "Report"}
    );

    private final MentionResourceTypeRepository repo;

    public ResourceTypeEventDefinitionSeedInitializer(MentionResourceTypeRepository repo) {
        this.repo = repo;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        for (String[] entry : SYSTEM_TYPES) {
            String code = entry[0];
            String displayName = entry[1];
            if (!repo.existsByCode(code)) {
                repo.save(MentionResourceTypeDefinition.create(code, displayName, null, true));
                log.info("Seeded system resource type: {}", code);
            }
        }
    }
}
