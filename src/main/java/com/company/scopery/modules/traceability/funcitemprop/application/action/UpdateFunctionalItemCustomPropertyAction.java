package com.company.scopery.modules.traceability.funcitemprop.application.action;

import com.company.scopery.modules.traceability.funcitemprop.application.command.UpdateFunctionalItemCustomPropertyCommand;
import com.company.scopery.modules.traceability.funcitemprop.application.response.FunctionalItemCustomPropertyResponse;
import com.company.scopery.modules.traceability.funcitemprop.domain.enums.CustomPropertyFieldType;
import com.company.scopery.modules.traceability.funcitemprop.domain.model.FunctionalItemCustomProperty;
import com.company.scopery.modules.traceability.funcitemprop.domain.model.FunctionalItemCustomPropertyRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateFunctionalItemCustomPropertyAction {

    private final FunctionalItemCustomPropertyRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public UpdateFunctionalItemCustomPropertyAction(
            FunctionalItemCustomPropertyRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public FunctionalItemCustomPropertyResponse execute(UpdateFunctionalItemCustomPropertyCommand c) {
        authorization.requireCreate(c.projectId());

        FunctionalItemCustomProperty existing = repo.findByIdAndFunctionalItemId(c.id(), c.functionalItemId())
                .orElseThrow(() -> TraceabilityExceptions.funcItemCustomPropNotFound(c.id()));

        CustomPropertyFieldType fieldType = TraceabilityEnumParser.parseRequired(
                CustomPropertyFieldType.class, c.fieldType(), "fieldType");

        FunctionalItemCustomProperty updated = existing.withUpdated(c.propValue(), fieldType);

        return FunctionalItemCustomPropertyResponse.from(repo.save(updated));
    }
}
