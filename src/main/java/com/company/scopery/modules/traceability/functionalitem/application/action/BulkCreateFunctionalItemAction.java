package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.modules.traceability.functionalitem.application.command.BulkCreateFunctionalItemCommand;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BulkCreateFunctionalItemAction {

    private final FunctionalItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public BulkCreateFunctionalItemAction(FunctionalItemRepository repo,
                                           TraceabilityAuthorizationService authorization,
                                           ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public List<FunctionalItemResponse> execute(BulkCreateFunctionalItemCommand cmd) {
        authorization.requireCreate(cmd.projectId());

        List<FunctionalItemResponse> results = new ArrayList<>();

        for (CreateFunctionalItemCommand item : cmd.items()) {
            if (repo.existsByProjectIdAndCode(cmd.projectId(), item.code())) {
                throw TraceabilityExceptions.functionalItemCodeExists(item.code());
            }

            FunctionalItemPriority priority = TraceabilityEnumParser.parseRequired(
                    FunctionalItemPriority.class, item.priority(), "priority");
            FunctionalItemType type = TraceabilityEnumParser.parseRequired(
                    FunctionalItemType.class, item.type(), "type");

            FunctionalItem fi = FunctionalItem.create(
                    cmd.projectId(),
                    item.workspaceId(),
                    item.moduleId(),
                    item.code().trim(),
                    item.title().trim(),
                    item.description(),
                    priority,
                    type,
                    item.acceptanceCriteria()
            );

            FunctionalItem saved = repo.save(fi);
            publisher.publishEvent(Map.of(
                    "eventCode", "FUNCTIONAL_ITEM_SAVED",
                    "entityId", saved.id(),
                    "projectId", saved.projectId(),
                    "workspaceId", saved.workspaceId()
            ));
            results.add(FunctionalItemResponse.from(saved));
        }

        return results;
    }
}
