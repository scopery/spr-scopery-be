package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.modules.traceability.businessrule.application.command.CreateBusinessRuleCommand;
import com.company.scopery.modules.traceability.businessrule.domain.enums.BusinessRuleSeverity;
import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRule;
import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRuleRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CreateFunctionalItemAction {

    private final FunctionalItemRepository repo;
    private final BusinessRuleRepository businessRuleRepo;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public CreateFunctionalItemAction(FunctionalItemRepository repo,
                                      BusinessRuleRepository businessRuleRepo,
                                      TraceabilityAuthorizationService authorization,
                                      ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.businessRuleRepo = businessRuleRepo;
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

        createNestedBusinessRules(saved.id(), c.projectId(), c.businessRules());

        publisher.publishEvent(Map.of(
                "eventCode", "FUNCTIONAL_ITEM_SAVED",
                "entityId", saved.id(),
                "projectId", saved.projectId(),
                "workspaceId", saved.workspaceId()
        ));
        return FunctionalItemResponse.from(saved);
    }

    private void createNestedBusinessRules(java.util.UUID functionalItemId, java.util.UUID projectId,
                                            List<CreateBusinessRuleCommand> rules) {
        if (rules == null || rules.isEmpty()) return;

        Set<String> seenCodes = new HashSet<>();
        for (CreateBusinessRuleCommand rule : rules) {
            if (!seenCodes.add(rule.code())) {
                throw TraceabilityExceptions.businessRuleCodeExists(rule.code());
            }
        }

        for (CreateBusinessRuleCommand rule : rules) {
            BusinessRuleSeverity severity = TraceabilityEnumParser.parseRequired(
                    BusinessRuleSeverity.class, rule.severity(), "severity");
            businessRuleRepo.save(BusinessRule.create(
                    functionalItemId, projectId, rule.code(), rule.title(), rule.description(), severity));
        }
    }
}
