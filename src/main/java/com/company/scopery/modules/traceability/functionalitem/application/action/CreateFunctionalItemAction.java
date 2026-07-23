package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.modules.traceability.functionalitem.application.command.CreateFunctionalItemCommand;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemResponse;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemPriority;
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
public class CreateFunctionalItemAction {

    private final FunctionalItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public CreateFunctionalItemAction(FunctionalItemRepository repo,
                                      TraceabilityAuthorizationService authorization,
                                      ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public FunctionalItemResponse execute(CreateFunctionalItemCommand c) {
        authorization.requireCreate(c.projectId());

        if (repo.existsByProjectIdAndCode(c.projectId(), c.code())) {
            throw TraceabilityExceptions.functionalItemCodeExists(c.code());
        }

        FunctionalItemPriority priority = TraceabilityEnumParser.parseRequired(FunctionalItemPriority.class, c.priority(), "priority");
        FunctionalItemType type = TraceabilityEnumParser.parseRequired(FunctionalItemType.class, c.type(), "type");

        FunctionalItem item = FunctionalItem.create(
                c.projectId(),
                c.workspaceId(),
                c.moduleId(),
                c.code().trim(),
                c.title().trim(),
                c.description(),
                priority,
                type,
                c.acceptanceCriteria()
        );

        FunctionalItem saved = repo.save(item);
        publisher.publishEvent(Map.of(
                "eventCode", "FUNCTIONAL_ITEM_SAVED",
                "entityId", saved.id(),
                "projectId", saved.projectId(),
                "workspaceId", saved.workspaceId()
        ));
        return FunctionalItemResponse.from(saved);
    }
}
