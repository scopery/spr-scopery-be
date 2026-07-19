package com.company.scopery.modules.traceability.requirementversion.application.action;
import com.company.scopery.modules.traceability.requirementversion.application.command.CreateRequirementVersionCommand;
import com.company.scopery.modules.traceability.requirementversion.application.response.RequirementVersionResponse;
import com.company.scopery.modules.traceability.requirementversion.domain.model.RequirementVersion;
import com.company.scopery.modules.traceability.requirementversion.domain.model.RequirementVersionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRequirementVersionAction {
    private final RequirementVersionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRequirementVersionAction(RequirementVersionRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional
    public RequirementVersionResponse execute(CreateRequirementVersionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        int nextVersion = repo.countByRequirementId(c.requirementId()) + 1;
        return RequirementVersionResponse.from(repo.save(RequirementVersion.create(c.requirementId(), c.workspaceId(), nextVersion, c.title().trim(), c.description(), c.changeSummary(), c.createdByUserId())));
    }
}
