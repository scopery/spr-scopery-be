package com.company.scopery.modules.notification.advanced.digest.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.digest.application.response.DigestRunResponse;
import com.company.scopery.modules.notification.advanced.digest.domain.model.DigestRunRepository;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DigestRunQueryService {
    private final DigestRunRepository runs; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public DigestRunQueryService(DigestRunRepository runs, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.runs=runs; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional(readOnly=true)
    public List<DigestRunResponse> list(UUID workspaceId) {
        try { iam.requireWorkspaceAccess(workspaceId, currentUser.resolveCurrentUser().id(), IamAuthorities.DIGEST_RULE_MANAGE); }
        catch (RuntimeException ex) { throw AdvancedNotificationExceptions.accessDenied(); }
        return runs.findByWorkspaceId(workspaceId).stream().map(DigestRunResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<DigestRunResponse> listMine(UUID workspaceId) {
        var userId = currentUser.resolveCurrentUser().id();
        return runs.findByWorkspaceIdAndRecipientUserId(workspaceId, userId).stream().map(DigestRunResponse::from).toList();
    }
}
