package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.modules.traceability.functionalitem.application.command.ExecuteFunctionalItemsImportCommand;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemImportResultResponse;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemPriority;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemStatus;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemType;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemEntry;
import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemUpdateEntry;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class ExecuteFunctionalItemsImportAction {

    private final FunctionalItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;
    private final ApplicationEventPublisher publisher;

    public ExecuteFunctionalItemsImportAction(FunctionalItemRepository repo,
                                              TraceabilityAuthorizationService authorization,
                                              TraceabilityActivityLogger activityLogger,
                                              ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.publisher = publisher;
    }

    @Transactional
    public FunctionalItemImportResultResponse execute(ExecuteFunctionalItemsImportCommand c) {
        authorization.requireCreate(c.projectId());

        List<ImportFunctionalItemEntry> createEntries = c.toCreate() != null ? c.toCreate() : List.of();
        List<ImportFunctionalItemUpdateEntry> updateEntries = c.toUpdate() != null ? c.toUpdate() : List.of();

        Set<UUID> touchedIds = new HashSet<>();
        int created = 0;
        int updated = 0;

        for (ImportFunctionalItemEntry entry : createEntries) {
            if (entry.title() == null || entry.title().isBlank()) {
                throw TraceabilityExceptions.importInvalidFunctionalItem("Title must not be blank");
            }

            FunctionalItemPriority priority = TraceabilityEnumParser.parseRequired(
                    FunctionalItemPriority.class, entry.priority(), "priority");
            FunctionalItemType type = TraceabilityEnumParser.parseRequired(
                    FunctionalItemType.class, entry.type(), "type");

            String code = (entry.code() != null && !entry.code().isBlank()) ? entry.code().trim() : generateCode();

            FunctionalItem item = FunctionalItem.create(
                    c.projectId(),
                    entry.workspaceId(),
                    entry.moduleId(),
                    code,
                    entry.title().trim(),
                    entry.description(),
                    priority,
                    type,
                    entry.acceptanceCriteria()
            );

            FunctionalItem saved = repo.save(item);
            touchedIds.add(saved.id());
            publishSaved(saved);
            created++;
        }

        for (ImportFunctionalItemUpdateEntry entry : updateEntries) {
            FunctionalItem existing = repo.findByIdAndProjectId(entry.existingItemId(), c.projectId())
                    .orElseThrow(() -> TraceabilityExceptions.importFunctionalItemNotFound(entry.existingItemId()));

            FunctionalItemPriority priority = entry.priority() != null
                    ? TraceabilityEnumParser.parseRequired(FunctionalItemPriority.class, entry.priority(), "priority")
                    : existing.priority();
            FunctionalItemStatus status = entry.status() != null
                    ? TraceabilityEnumParser.parseRequired(FunctionalItemStatus.class, entry.status(), "status")
                    : existing.status();
            FunctionalItemType type = entry.type() != null
                    ? TraceabilityEnumParser.parseRequired(FunctionalItemType.class, entry.type(), "type")
                    : existing.type();

            String title = (entry.title() != null && !entry.title().isBlank()) ? entry.title().trim() : existing.title();
            String description = entry.description() != null ? entry.description() : existing.description();
            UUID moduleId = entry.moduleId() != null ? entry.moduleId() : existing.moduleId();
            List<String> criteria = entry.acceptanceCriteria() != null ? entry.acceptanceCriteria() : existing.acceptanceCriteria();

            FunctionalItem updatedItem = existing.withUpdated(moduleId, title, description, priority, status, type, criteria);
            FunctionalItem saved = repo.save(updatedItem);
            touchedIds.add(saved.id());
            publishSaved(saved);
            updated++;
        }

        int archived = 0;
        if (c.archiveUnmatched()) {
            List<FunctionalItem> allExisting = repo.findByProjectId(c.projectId());
            for (FunctionalItem item : allExisting) {
                if (!touchedIds.contains(item.id()) && item.status() != FunctionalItemStatus.ARCHIVED) {
                    FunctionalItem archivedItem = item.withUpdated(
                            item.moduleId(), item.title(), item.description(),
                            item.priority(), FunctionalItemStatus.ARCHIVED, item.type(), item.acceptanceCriteria());
                    repo.save(archivedItem);
                    archived++;
                }
            }
        }

        activityLogger.logSuccess(TraceabilityEntityTypes.FUNCTIONAL_ITEM, c.projectId(),
                TraceabilityActivityActions.FUNCTIONAL_ITEMS_IMPORTED,
                "Imported: " + created + " created, " + updated + " updated, " + archived + " archived");

        return new FunctionalItemImportResultResponse(created, updated, archived);
    }

    private void publishSaved(FunctionalItem saved) {
        publisher.publishEvent(Map.of(
                "eventCode", "FUNCTIONAL_ITEM_SAVED",
                "entityId", saved.id(),
                "projectId", saved.projectId(),
                "workspaceId", saved.workspaceId() != null ? saved.workspaceId() : ""
        ));
    }

    private String generateCode() {
        return "FI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
