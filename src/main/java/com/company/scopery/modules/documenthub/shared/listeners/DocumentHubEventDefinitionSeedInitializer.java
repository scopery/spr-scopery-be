package com.company.scopery.modules.documenthub.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent; import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order; import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; import java.util.List;
@Component @Order(33)
public class DocumentHubEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    public static final String SOURCE_SYSTEM = "SCOPERY_DOCUMENT_HUB";
    private final EventDefinitionRepository repo;
    public DocumentHubEventDefinitionSeedInitializer(EventDefinitionRepository repo) { this.repo = repo; }
    @Override @Transactional public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(
                new Seed("DOCUMENT_CREATED", "Document Created", "Document created"),
                new Seed("DOCUMENT_APPROVED", "Document Approved", "Document approved"),
                new Seed("DOCUMENT_VERSION_UPLOADED", "Document Version Uploaded", "Version uploaded"),
                new Seed("DOCUMENT_GENERATION_REQUESTED", "Document Generation Requested", "Generation requested"),
                new Seed("DOCUMENT_GENERATION_COMPLETED", "Document Generation Completed", "Generation completed"),
                // Native editor event seeds
                new Seed("DOCUMENT_CONTENT_SAVED", "Document Content Saved", "Native document content saved"),
                new Seed("DOCUMENT_MENTION_EXTRACTED", "Document Mention Extracted", "Resource mention extracted from document"),
                new Seed("DOCUMENT_COMMENT_THREAD_OPENED", "Document Comment Thread Opened", "Comment thread opened on document"),
                new Seed("DOCUMENT_COMMENT_THREAD_RESOLVED", "Document Comment Thread Resolved", "Comment thread resolved"),
                new Seed("DOCUMENT_SUGGESTION_CREATED", "Document Suggestion Created", "Content suggestion created"),
                new Seed("DOCUMENT_SUGGESTION_ACCEPTED", "Document Suggestion Accepted", "Content suggestion accepted"),
                new Seed("DOCUMENT_SUGGESTION_REJECTED", "Document Suggestion Rejected", "Content suggestion rejected"),
                new Seed("SYNCED_BLOCK_UPDATED", "Synced Block Updated", "Synced block content updated"),
                new Seed("DOCUMENT_CLIENT_VISIBILITY_VALIDATED", "Document Client Visibility Validated", "Client visibility validation completed")
        )) EventDefinitionSeedSupport.findOrCreate(repo, SOURCE_SYSTEM, s.code, s.name, s.desc, EventDataClassification.INTERNAL, "DOCUMENT_HUB");
    }
    private record Seed(String code, String name, String desc) {}
}
