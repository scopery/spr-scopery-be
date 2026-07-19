package com.company.scopery.modules.clientportal.grant.application.action;
import com.company.scopery.modules.clientportal.grant.application.command.RevokePortalAccessGrantCommand;
import com.company.scopery.modules.clientportal.grant.application.response.ExternalProjectAccessGrantResponse;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrantRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RevokePortalAccessGrantAction {
    private final ExternalProjectAccessGrantRepository repo;
    private final ClientPortalAuthorizationService authorization;
    private final ClientPortalActivityLogger activityLogger;
    public RevokePortalAccessGrantAction(ExternalProjectAccessGrantRepository repo, ClientPortalAuthorizationService authorization, ClientPortalActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public ExternalProjectAccessGrantResponse execute(RevokePortalAccessGrantCommand c) {
        authorization.requireManage(c.projectId());
        var e = repo.findByIdAndProjectId(c.grantId(), c.projectId()).orElseThrow(() -> ClientPortalExceptions.grantNotFound(c.grantId()));
        var saved = repo.save(e.revoke());
        activityLogger.logSuccess(ClientPortalEntityTypes.ACCESS_GRANT, saved.id(), ClientPortalActivityActions.GRANT_REVOKED, "Portal grant revoked");
        return ExternalProjectAccessGrantResponse.from(saved);
    }
}
