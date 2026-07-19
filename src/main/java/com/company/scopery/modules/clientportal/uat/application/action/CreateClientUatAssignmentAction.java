package com.company.scopery.modules.clientportal.uat.application.action;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import com.company.scopery.modules.clientportal.uat.application.command.CreateClientUatAssignmentCommand;
import com.company.scopery.modules.clientportal.uat.application.response.ClientUatAssignmentResponse;
import com.company.scopery.modules.clientportal.uat.domain.model.ClientUatAssignment;
import com.company.scopery.modules.clientportal.uat.domain.model.ClientUatAssignmentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateClientUatAssignmentAction {
    private final ClientUatAssignmentRepository repo;
    private final ClientPortalAuthorizationService authorization;
    public CreateClientUatAssignmentAction(ClientUatAssignmentRepository repo, ClientPortalAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public ClientUatAssignmentResponse execute(CreateClientUatAssignmentCommand c) {
        authorization.requireManage(c.projectId());
        return ClientUatAssignmentResponse.from(repo.save(ClientUatAssignment.create(c.projectId(), c.testCaseId(), c.testRunId(), c.portalAccountId(), c.notes())));
    }
}
