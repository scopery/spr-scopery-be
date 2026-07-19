package com.company.scopery.modules.traceability.requirementcriteria.application.action;
import com.company.scopery.modules.traceability.requirementcriteria.application.command.CreateRequirementCriteriaCommand;
import com.company.scopery.modules.traceability.requirementcriteria.application.response.RequirementCriteriaResponse;
import com.company.scopery.modules.traceability.requirementcriteria.domain.enums.RequirementCriteriaType;
import com.company.scopery.modules.traceability.requirementcriteria.domain.model.RequirementCriteria;
import com.company.scopery.modules.traceability.requirementcriteria.domain.model.RequirementCriteriaRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRequirementCriteriaAction {
    private final RequirementCriteriaRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRequirementCriteriaAction(RequirementCriteriaRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional
    public RequirementCriteriaResponse execute(CreateRequirementCriteriaCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        RequirementCriteriaType type = TraceabilityEnumParser.parseRequired(RequirementCriteriaType.class, c.acceptanceType(), "acceptanceType");
        return RequirementCriteriaResponse.from(repo.save(RequirementCriteria.create(c.requirementId(), c.workspaceId(), c.description().trim(), type, c.displayOrder())));
    }
}
