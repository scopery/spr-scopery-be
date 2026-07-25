package com.company.scopery.modules.traceability.funcitemprop.application.action;

import com.company.scopery.modules.traceability.funcitemprop.application.command.BulkCreateFunctionalItemCustomPropertyCommand;
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

import java.util.ArrayList;
import java.util.List;

@Component
public class BulkCreateFunctionalItemCustomPropertyAction {

    private final FunctionalItemCustomPropertyRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public BulkCreateFunctionalItemCustomPropertyAction(
            FunctionalItemCustomPropertyRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public List<FunctionalItemCustomPropertyResponse> execute(BulkCreateFunctionalItemCustomPropertyCommand cmd) {
        authorization.requireCreate(cmd.projectId());

        List<FunctionalItemCustomPropertyResponse> results = new ArrayList<>();

        for (CreateFunctionalItemCustomPropertyCommand item : cmd.items()) {
            if (repo.existsByFunctionalItemIdAndPropKey(cmd.functionalItemId(), item.propKey())) {
                throw TraceabilityExceptions.funcItemCustomPropKeyExists(item.propKey());
            }

            CustomPropertyFieldType fieldType = TraceabilityEnumParser.parseRequired(
                    CustomPropertyFieldType.class, item.fieldType(), "fieldType");

            FunctionalItemCustomProperty prop = FunctionalItemCustomProperty.create(
                    cmd.functionalItemId(), item.propKey(), item.propValue(), fieldType);

            results.add(FunctionalItemCustomPropertyResponse.from(repo.save(prop)));
        }

        return results;
    }
}
