package com.company.scopery.modules.clientportal.comment.application.action;
import com.company.scopery.modules.clientportal.comment.application.command.CreateClientCommentCommand;
import com.company.scopery.modules.clientportal.comment.application.response.ClientCommentResponse;
import com.company.scopery.modules.clientportal.comment.domain.model.ClientComment;
import com.company.scopery.modules.clientportal.comment.domain.model.ClientCommentRepository;
import com.company.scopery.modules.clientportal.shared.security.CurrentPortalAccountService;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateClientCommentAction {
    private final ClientCommentRepository repo;
    private final PortalGrantEnforcementService grantEnforcement;
    private final CurrentPortalAccountService currentPortalAccount;
    public CreateClientCommentAction(ClientCommentRepository repo, PortalGrantEnforcementService grantEnforcement, CurrentPortalAccountService currentPortalAccount) {
        this.repo=repo; this.grantEnforcement=grantEnforcement; this.currentPortalAccount=currentPortalAccount;
    }
    @Transactional
    public ClientCommentResponse execute(CreateClientCommentCommand c) {
        grantEnforcement.requireActiveGrant(c.projectId());
        var author = currentPortalAccount.requireCurrentPortalAccountId();
        return ClientCommentResponse.from(repo.save(ClientComment.create(c.projectId(), c.targetType().trim(), c.targetId(), c.body().trim(), author)));
    }
}
