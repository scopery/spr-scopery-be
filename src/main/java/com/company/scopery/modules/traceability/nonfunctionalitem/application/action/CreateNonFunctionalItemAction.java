package com.company.scopery.modules.traceability.nonfunctionalitem.application.action;

import com.company.scopery.modules.traceability.nonfunctionalitem.application.command.CreateNonFunctionalItemCommand;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.response.NonFunctionalItemResponse;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrCategory;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrPriority;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrScopeType;
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
public class CreateNonFunctionalItemAction {

    private final NonFunctionalItemRepository repository;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public CreateNonFunctionalItemAction(
            NonFunctionalItemRepository repository,
            TraceabilityAuthorizationService authorization,
            ApplicationEventPublisher publisher
    ) {
        this.repository = repository;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public NonFunctionalItemResponse execute(CreateNonFunctionalItemCommand command) {
        authorization.requireCreate(command.projectId());

        if (repository.existsByProjectIdAndCode(command.projectId(), command.code())) {
            throw TraceabilityExceptions.nonFunctionalItemCodeExists(command.code());
        }

        NfrCategory category = TraceabilityEnumParser.parseRequired(NfrCategory.class, command.category(), "category");
        NfrPriority priority = TraceabilityEnumParser.parseRequired(NfrPriority.class, command.priority(), "priority");
        NfrScopeType scopeType = TraceabilityEnumParser.parseRequired(NfrScopeType.class, command.scopeType(), "scopeType");

        NonFunctionalItem item = NonFunctionalItem.create(
                command.projectId(),
                command.workspaceId(),
                command.code(),
                command.title(),
                command.description(),
                category,
                priority,
                command.targetMetric(),
                scopeType,
                command.scopeRefId()
        );

        NonFunctionalItem saved = repository.save(item);
        publisher.publishEvent(Map.of(
                "eventCode", "NON_FUNCTIONAL_ITEM_SAVED",
                "entityId", saved.id(),
                "projectId", saved.projectId(),
                "workspaceId", saved.workspaceId()
        ));
        return NonFunctionalItemResponse.from(saved);
    }
}
