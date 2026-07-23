package com.company.scopery.modules.traceability.businessrule.application.action;

import com.company.scopery.modules.traceability.businessrule.application.command.UpdateBusinessRuleCommand;
import com.company.scopery.modules.traceability.businessrule.application.response.BusinessRuleResponse;
import com.company.scopery.modules.traceability.businessrule.domain.enums.BusinessRuleSeverity;
import com.company.scopery.modules.traceability.businessrule.domain.enums.BusinessRuleStatus;
import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRule;
import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRuleRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateBusinessRuleAction {

    private final BusinessRuleRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public UpdateBusinessRuleAction(
            BusinessRuleRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public BusinessRuleResponse execute(UpdateBusinessRuleCommand c) {
        authorization.requireCreate(c.projectId());

        BusinessRule existing = repo.findByIdAndFunctionalItemId(c.id(), c.functionalItemId())
                .orElseThrow(() -> TraceabilityExceptions.businessRuleNotFound(c.id()));

        BusinessRuleSeverity severity = TraceabilityEnumParser.parseRequired(
                BusinessRuleSeverity.class, c.severity(), "severity");
        BusinessRuleStatus status = TraceabilityEnumParser.parseRequired(
                BusinessRuleStatus.class, c.status(), "status");

        BusinessRule updated = existing.withUpdated(c.title(), c.description(), severity, status);

        return BusinessRuleResponse.from(repo.save(updated));
    }
}
