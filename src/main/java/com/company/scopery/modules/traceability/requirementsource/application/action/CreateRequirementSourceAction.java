package com.company.scopery.modules.traceability.requirementsource.application.action;
import com.company.scopery.modules.traceability.requirementsource.application.command.CreateRequirementSourceCommand;
import com.company.scopery.modules.traceability.requirementsource.application.response.RequirementSourceResponse;
import com.company.scopery.modules.traceability.requirementsource.domain.model.RequirementSource;
import com.company.scopery.modules.traceability.requirementsource.domain.model.RequirementSourceRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRequirementSourceAction {
    private final RequirementSourceRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRequirementSourceAction(RequirementSourceRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional
    public RequirementSourceResponse execute(CreateRequirementSourceCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        return RequirementSourceResponse.from(repo.save(RequirementSource.create(c.requirementId(), c.workspaceId(), c.sourceType().trim(), c.sourceReference().trim(), c.description())));
    }
}
