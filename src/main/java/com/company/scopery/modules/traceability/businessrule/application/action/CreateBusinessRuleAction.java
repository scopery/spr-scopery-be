package com.company.scopery.modules.traceability.businessrule.application.action;

import com.company.scopery.modules.traceability.businessrule.application.command.CreateBusinessRuleCommand;
import com.company.scopery.modules.traceability.businessrule.application.response.BusinessRuleResponse;
import com.company.scopery.modules.traceability.businessrule.domain.enums.BusinessRuleSeverity;
import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRule;
import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRuleRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateBusinessRuleAction {

    private final BusinessRuleRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public CreateBusinessRuleAction(
            BusinessRuleRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public BusinessRuleResponse execute(CreateBusinessRuleCommand c) {
        authorization.requireCreate(c.projectId());

        if (repo.existsByFunctionalItemIdAndCode(c.functionalItemId(), c.code())) {
            throw TraceabilityExceptions.businessRuleCodeExists(c.code());
        }

        BusinessRuleSeverity severity = TraceabilityEnumParser.parseRequired(
                BusinessRuleSeverity.class, c.severity(), "severity");

        BusinessRule rule = BusinessRule.create(
                c.functionalItemId(),
                c.projectId(),
                c.code(),
                c.title(),
                c.description(),
                severity
        );

        return BusinessRuleResponse.from(repo.save(rule));
    }
}
