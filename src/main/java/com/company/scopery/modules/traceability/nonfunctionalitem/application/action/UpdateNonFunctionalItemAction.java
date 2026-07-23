package com.company.scopery.modules.traceability.nonfunctionalitem.application.action;

import com.company.scopery.modules.traceability.nonfunctionalitem.application.command.UpdateNonFunctionalItemCommand;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.response.NonFunctionalItemResponse;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrCategory;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrPriority;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrScopeType;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrStatus;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItem;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class UpdateNonFunctionalItemAction {

    private final NonFunctionalItemRepository repository;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public UpdateNonFunctionalItemAction(
            NonFunctionalItemRepository repository,
            TraceabilityAuthorizationService authorization,
            ApplicationEventPublisher publisher
    ) {
        this.repository = repository;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public NonFunctionalItemResponse execute(UpdateNonFunctionalItemCommand command) {
        authorization.requireCreate(command.projectId());

        NonFunctionalItem existing = repository.findByIdAndProjectId(command.id(), command.projectId())
                .orElseThrow(() -> TraceabilityExceptions.nonFunctionalItemNotFound(command.id()));

        NfrCategory category = TraceabilityEnumParser.parseRequired(NfrCategory.class, command.category(), "category");
        NfrPriority priority = TraceabilityEnumParser.parseRequired(NfrPriority.class, command.priority(), "priority");
        NfrStatus status = TraceabilityEnumParser.parseRequired(NfrStatus.class, command.status(), "status");
        NfrScopeType scopeType = TraceabilityEnumParser.parseRequired(NfrScopeType.class, command.scopeType(), "scopeType");

        NonFunctionalItem updated = existing.withUpdated(
                command.title(),
                command.description(),
                category,
                priority,
                status,
                command.targetMetric(),
                scopeType,
                command.scopeRefId()
        );

        NonFunctionalItem saved = repository.save(updated);
        publisher.publishEvent(Map.of(
                "eventCode", "NON_FUNCTIONAL_ITEM_SAVED",
                "entityId", saved.id(),
                "projectId", saved.projectId(),
                "workspaceId", saved.workspaceId()
        ));
        return NonFunctionalItemResponse.from(saved);
    }
}
