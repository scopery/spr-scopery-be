package com.company.scopery.modules.knowledge.source.application.listeners;

import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.AppModuleKnowledgeSourceAdapter;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.FunctionalItemKnowledgeSourceAdapter;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.NonFunctionalItemKnowledgeSourceAdapter;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.RequirementKnowledgeSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class TraceabilityKnowledgeIndexingListener {

    private static final Logger log = LoggerFactory.getLogger(TraceabilityKnowledgeIndexingListener.class);

    private final FunctionalItemKnowledgeSourceAdapter functionalItemAdapter;
    private final NonFunctionalItemKnowledgeSourceAdapter nfrAdapter;
    private final AppModuleKnowledgeSourceAdapter appModuleAdapter;
    private final RequirementKnowledgeSourceAdapter requirementAdapter;
    private final KnowledgeSourceIndexingService indexingService;

    public TraceabilityKnowledgeIndexingListener(
            FunctionalItemKnowledgeSourceAdapter functionalItemAdapter,
            NonFunctionalItemKnowledgeSourceAdapter nfrAdapter,
            AppModuleKnowledgeSourceAdapter appModuleAdapter,
            RequirementKnowledgeSourceAdapter requirementAdapter,
            KnowledgeSourceIndexingService indexingService) {
        this.functionalItemAdapter = functionalItemAdapter;
        this.nfrAdapter = nfrAdapter;
        this.appModuleAdapter = appModuleAdapter;
        this.requirementAdapter = requirementAdapter;
        this.indexingService = indexingService;
    }

    @EventListener(condition = "#event['eventCode'] == 'FUNCTIONAL_ITEM_SAVED'")
    @Async
    public void onFunctionalItemSaved(Map<String, Object> event) {
        UUID projectId = extractUuid(event, "projectId");
        UUID itemId = extractUuid(event, "entityId");
        if (projectId == null || itemId == null) return;
        try {
            functionalItemAdapter.buildSnapshot(projectId, itemId)
                    .ifPresent(indexingService::upsertSource);
        } catch (Exception e) {
            log.warn("FunctionalItem index failed for {}: {}", itemId, e.getMessage());
        }
    }

    @EventListener(condition = "#event['eventCode'] == 'NON_FUNCTIONAL_ITEM_SAVED'")
    @Async
    public void onNonFunctionalItemSaved(Map<String, Object> event) {
        UUID projectId = extractUuid(event, "projectId");
        UUID itemId = extractUuid(event, "entityId");
        if (projectId == null || itemId == null) return;
        try {
            nfrAdapter.buildSnapshot(projectId, itemId)
                    .ifPresent(indexingService::upsertSource);
        } catch (Exception e) {
            log.warn("NonFunctionalItem index failed for {}: {}", itemId, e.getMessage());
        }
    }

    @EventListener(condition = "#event['eventCode'] == 'APP_MODULE_SAVED'")
    @Async
    public void onAppModuleSaved(Map<String, Object> event) {
        UUID workspaceId = extractUuid(event, "workspaceId");
        UUID moduleId = extractUuid(event, "entityId");
        if (workspaceId == null || moduleId == null) return;
        try {
            appModuleAdapter.buildSnapshot(workspaceId, moduleId)
                    .ifPresent(indexingService::upsertSource);
        } catch (Exception e) {
            log.warn("AppModule index failed for {}: {}", moduleId, e.getMessage());
        }
    }

    @EventListener(condition = "#event['eventCode'] == 'REQUIREMENT_SAVED'")
    @Async
    public void onRequirementSaved(Map<String, Object> event) {
        UUID projectId = extractUuid(event, "projectId");
        UUID requirementId = extractUuid(event, "entityId");
        if (projectId == null || requirementId == null) return;
        try {
            requirementAdapter.buildSnapshot(projectId, requirementId)
                    .ifPresent(indexingService::upsertSource);
        } catch (Exception e) {
            log.warn("Requirement index failed for {}: {}", requirementId, e.getMessage());
        }
    }

    private UUID extractUuid(Map<String, Object> event, String key) {
        Object val = event.get(key);
        if (val == null) return null;
        try {
            return val instanceof UUID u ? u : UUID.fromString(val.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
