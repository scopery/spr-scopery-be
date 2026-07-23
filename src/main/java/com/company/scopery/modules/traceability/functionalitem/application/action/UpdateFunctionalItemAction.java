package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.modules.traceability.functionalitem.application.command.UpdateFunctionalItemCommand;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemResponse;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemPriority;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemStatus;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemType;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class UpdateFunctionalItemAction {

    private final FunctionalItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public UpdateFunctionalItemAction(FunctionalItemRepository repo,
                                      TraceabilityAuthorizationService authorization,
                                      ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public FunctionalItemResponse execute(UpdateFunctionalItemCommand c) {
        authorization.requireCreate(c.projectId());

        FunctionalItem existing = repo.findByIdAndProjectId(c.id(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(c.id()));

        FunctionalItemPriority priority = TraceabilityEnumParser.parseRequired(FunctionalItemPriority.class, c.priority(), "priority");
        FunctionalItemStatus status = TraceabilityEnumParser.parseRequired(FunctionalItemStatus.class, c.status(), "status");
        FunctionalItemType type = TraceabilityEnumParser.parseRequired(FunctionalItemType.class, c.type(), "type");

        FunctionalItem updated = existing.withUpdated(
                c.moduleId(),
                c.title().trim(),
                c.description(),
                priority,
                status,
                type,
                c.acceptanceCriteria()
        );

        FunctionalItem saved = repo.save(updated);
        publisher.publishEvent(Map.of(
                "eventCode", "FUNCTIONAL_ITEM_SAVED",
                "entityId", saved.id(),
                "projectId", saved.projectId(),
                "workspaceId", saved.workspaceId()
        ));
        return FunctionalItemResponse.from(saved);
    }
}
