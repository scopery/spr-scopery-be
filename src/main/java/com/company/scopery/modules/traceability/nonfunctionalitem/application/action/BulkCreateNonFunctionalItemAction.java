package com.company.scopery.modules.traceability.nonfunctionalitem.application.action;

import com.company.scopery.modules.traceability.nonfunctionalitem.application.command.BulkCreateNonFunctionalItemCommand;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BulkCreateNonFunctionalItemAction {

    private final NonFunctionalItemRepository repository;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public BulkCreateNonFunctionalItemAction(NonFunctionalItemRepository repository,
                                              TraceabilityAuthorizationService authorization,
                                              ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public List<NonFunctionalItemResponse> execute(BulkCreateNonFunctionalItemCommand cmd) {
        authorization.requireCreate(cmd.projectId());

        List<NonFunctionalItemResponse> results = new ArrayList<>();

        for (CreateNonFunctionalItemCommand item : cmd.items()) {
            if (repository.existsByProjectIdAndCode(cmd.projectId(), item.code())) {
                throw TraceabilityExceptions.nonFunctionalItemCodeExists(item.code());
            }

            NfrCategory category = TraceabilityEnumParser.parseRequired(
                    NfrCategory.class, item.category(), "category");
            NfrPriority priority = TraceabilityEnumParser.parseRequired(
                    NfrPriority.class, item.priority(), "priority");
            NfrScopeType scopeType = TraceabilityEnumParser.parseRequired(
                    NfrScopeType.class, item.scopeType(), "scopeType");

            NonFunctionalItem nfr = NonFunctionalItem.create(
                    cmd.projectId(),
                    item.workspaceId(),
                    item.code(),
                    item.title(),
                    item.description(),
                    category,
                    priority,
                    item.targetMetric(),
                    scopeType,
                    item.scopeRefId()
            );

            NonFunctionalItem saved = repository.save(nfr);
            publisher.publishEvent(Map.of(
                    "eventCode", "NON_FUNCTIONAL_ITEM_SAVED",
                    "entityId", saved.id(),
                    "projectId", saved.projectId(),
                    "workspaceId", saved.workspaceId()
            ));
            results.add(NonFunctionalItemResponse.from(saved));
        }

        return results;
    }
}
