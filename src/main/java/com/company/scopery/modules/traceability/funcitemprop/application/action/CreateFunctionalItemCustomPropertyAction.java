package com.company.scopery.modules.traceability.funcitemprop.application.action;

import com.company.scopery.modules.traceability.funcitemprop.application.command.CreateFunctionalItemCustomPropertyCommand;
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
public class CreateFunctionalItemCustomPropertyAction {

    private final FunctionalItemCustomPropertyRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public CreateFunctionalItemCustomPropertyAction(
            FunctionalItemCustomPropertyRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public FunctionalItemCustomPropertyResponse execute(CreateFunctionalItemCustomPropertyCommand c) {
        authorization.requireCreate(c.projectId());

        if (repo.existsByFunctionalItemIdAndPropKey(c.functionalItemId(), c.propKey())) {
            throw TraceabilityExceptions.funcItemCustomPropKeyExists(c.propKey());
        }

        CustomPropertyFieldType fieldType = TraceabilityEnumParser.parseRequired(
                CustomPropertyFieldType.class, c.fieldType(), "fieldType");

        FunctionalItemCustomProperty prop = FunctionalItemCustomProperty.create(
                c.functionalItemId(), c.propKey(), c.propValue(), fieldType);

        return FunctionalItemCustomPropertyResponse.from(repo.save(prop));
    }
}
