package com.company.scopery.modules.clientportal.feedback.application.action;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.clientportal.feedback.application.command.CreateClientFeedbackCommand;
import com.company.scopery.modules.clientportal.feedback.application.response.ClientFeedbackResponse;
import com.company.scopery.modules.clientportal.feedback.domain.model.ClientFeedback;
import com.company.scopery.modules.clientportal.feedback.domain.model.ClientFeedbackRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.security.CurrentPortalAccountService;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateClientFeedbackAction {
    private final ProjectRepository projects;
    private final ClientFeedbackRepository repo;
    private final ClientPortalAuthorizationService authorization;
    private final PortalGrantEnforcementService grantEnforcement;
    private final CurrentPortalAccountService currentPortalAccount;
    private final ClientPortalActivityLogger activityLogger;
    public CreateClientFeedbackAction(ProjectRepository projects, ClientFeedbackRepository repo,
            ClientPortalAuthorizationService authorization, PortalGrantEnforcementService grantEnforcement,
            CurrentPortalAccountService currentPortalAccount, ClientPortalActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization;
        this.grantEnforcement=grantEnforcement; this.currentPortalAccount=currentPortalAccount; this.activityLogger=activityLogger;
    }
    @Transactional
    public ClientFeedbackResponse execute(CreateClientFeedbackCommand c) {
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        UUID submitter;
        if (c.portalCaller()) {
            grantEnforcement.requireActiveGrant(c.projectId());
            submitter = currentPortalAccount.requireCurrentPortalAccountId();
        } else {
            authorization.requireManage(c.projectId());
            submitter = null;
        }
        var saved = repo.save(ClientFeedback.create(project.id(), project.workspaceId(), c.category().trim(), c.title().trim(), c.body(), submitter));
        activityLogger.logSuccess(ClientPortalEntityTypes.FEEDBACK, saved.id(), ClientPortalActivityActions.FEEDBACK_SUBMITTED, "Feedback submitted");
        return ClientFeedbackResponse.from(saved);
    }
}
