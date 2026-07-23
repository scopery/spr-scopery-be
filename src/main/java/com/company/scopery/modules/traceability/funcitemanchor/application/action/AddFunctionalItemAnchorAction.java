package com.company.scopery.modules.traceability.funcitemanchor.application.action;

import com.company.scopery.modules.traceability.funcitemanchor.application.command.AddFunctionalItemAnchorCommand;
import com.company.scopery.modules.traceability.funcitemanchor.application.response.FunctionalItemAnchorResponse;
import com.company.scopery.modules.traceability.funcitemanchor.domain.enums.AnchorNodeType;
import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchor;
import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchorRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddFunctionalItemAnchorAction {

    private final FunctionalItemAnchorRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public AddFunctionalItemAnchorAction(
            FunctionalItemAnchorRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public FunctionalItemAnchorResponse execute(AddFunctionalItemAnchorCommand c) {
        authorization.requireCreate(c.projectId());

        AnchorNodeType nodeType = TraceabilityEnumParser.parseRequired(
                AnchorNodeType.class, c.nodeType(), "nodeType");

        if (repo.existsByFunctionalItemIdAndNodeTypeAndNodeId(c.functionalItemId(), nodeType.name(), c.nodeId())) {
            throw TraceabilityExceptions.funcItemAnchorDuplicate();
        }

        FunctionalItemAnchor anchor = FunctionalItemAnchor.create(
                c.functionalItemId(), nodeType, c.nodeId(), c.note());

        return FunctionalItemAnchorResponse.from(repo.save(anchor));
    }
}
