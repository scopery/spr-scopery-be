package com.company.scopery.modules.knowledge.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds Knowledge DocumentType / DocumentTypeField events (Phase 08).
 */
@Component
@Order(14)
public class KnowledgeEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeEventDefinitionSeedInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_KNOWLEDGE";
    public static final String OWNER_MODULE = "KNOWLEDGE";

    private final EventDefinitionRepository eventDefinitionRepository;

    public KnowledgeEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedEvent seed : EVENTS) {
            EventDefinitionSeedSupport.findOrCreate(
                    eventDefinitionRepository,
                    SOURCE_SYSTEM,
                    seed.code(),
                    seed.name(),
                    seed.description(),
                    EventDataClassification.INTERNAL,
                    OWNER_MODULE);
        }
        log.info("[KnowledgeEventDefinitionSeed] Knowledge event seeding complete ({} events)", EVENTS.size());
    }

    private record SeedEvent(String code, String name, String description) {}

    private static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("DOCUMENT_TYPE_CREATED", "Document Type Created", "Document type created"),
            new SeedEvent("DOCUMENT_TYPE_UPDATED", "Document Type Updated", "Document type updated"),
            new SeedEvent("DOCUMENT_TYPE_ACTIVATED", "Document Type Activated", "Document type activated"),
            new SeedEvent("DOCUMENT_TYPE_DEACTIVATED", "Document Type Deactivated", "Document type deactivated"),
            new SeedEvent("DOCUMENT_TYPE_ARCHIVED", "Document Type Archived", "Document type archived"),
            new SeedEvent("DOCUMENT_TYPE_DELETED", "Document Type Deleted", "Legacy soft-delete event (maps to archive)"),
            new SeedEvent("DOCUMENT_TYPE_FIELD_CREATED", "Document Type Field Created", "Document type field created"),
            new SeedEvent("DOCUMENT_TYPE_FIELD_UPDATED", "Document Type Field Updated", "Document type field updated"),
            new SeedEvent("DOCUMENT_TYPE_FIELD_ACTIVATED", "Document Type Field Activated", "Document type field activated"),
            new SeedEvent("DOCUMENT_TYPE_FIELD_DEACTIVATED", "Document Type Field Deactivated", "Document type field deactivated"),
            new SeedEvent("DOCUMENT_TYPE_FIELD_ARCHIVED", "Document Type Field Archived", "Document type field archived"),
            new SeedEvent("DOCUMENT_TYPE_FIELDS_REORDERED", "Document Type Fields Reordered", "Document type fields reordered"),

            // Phase 41 — Semantic index lifecycle
            new SeedEvent("KNOWLEDGE_SOURCE_DISCOVERED", "Knowledge Source Discovered", "A new source was discovered for indexing"),
            new SeedEvent("KNOWLEDGE_SOURCE_PROJECTED", "Knowledge Source Projected", "Text extraction completed for source"),
            new SeedEvent("KNOWLEDGE_SOURCE_INDEXED", "Knowledge Source Indexed", "Source successfully indexed in ES"),
            new SeedEvent("KNOWLEDGE_SOURCE_INDEX_FAILED", "Knowledge Source Index Failed", "Indexing failed for source"),
            new SeedEvent("KNOWLEDGE_SOURCE_INVALIDATED", "Knowledge Source Invalidated", "Source invalidated and removed from index"),
            new SeedEvent("KNOWLEDGE_REINDEX_REQUESTED", "Knowledge Reindex Requested", "Manual reindex triggered"),
            new SeedEvent("KNOWLEDGE_REINDEX_COMPLETED", "Knowledge Reindex Completed", "Reindex job completed"),
            new SeedEvent("KNOWLEDGE_REINDEX_FAILED", "Knowledge Reindex Failed", "Reindex job failed"),
            new SeedEvent("KNOWLEDGE_INDEX_ALIAS_SWITCHED", "Knowledge Index Alias Switched", "ES write/read alias switched to new generation")
    );
}
