package com.company.scopery.modules.clientportal.policy.application.action;
import com.company.scopery.modules.clientportal.policy.application.command.CreatePermissionPolicyCommand;
import com.company.scopery.modules.clientportal.policy.application.response.ExternalPortalPermissionPolicyResponse;
import com.company.scopery.modules.clientportal.policy.domain.model.ExternalPortalPermissionPolicy;
import com.company.scopery.modules.clientportal.policy.domain.model.ExternalPortalPermissionPolicyRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreatePermissionPolicyAction {
    private final ExternalPortalPermissionPolicyRepository repo;
    private final ClientPortalActivityLogger activityLogger;
    public CreatePermissionPolicyAction(ExternalPortalPermissionPolicyRepository repo, ClientPortalActivityLogger activityLogger) {
        this.repo = repo; this.activityLogger = activityLogger;
    }
    @Transactional
    public ExternalPortalPermissionPolicyResponse execute(CreatePermissionPolicyCommand c) {
        if (repo.existsByWorkspaceIdAndCode(c.workspaceId(), c.code().toUpperCase().trim()))
            throw ClientPortalExceptions.permissionPolicyCodeExists(c.code());
        var saved = repo.save(ExternalPortalPermissionPolicy.create(c.workspaceId(), c.code(), c.name(), c.description(), c.permissionsJson()));
        activityLogger.logSuccess(ClientPortalEntityTypes.PERMISSION_POLICY, saved.id(), ClientPortalActivityActions.PERMISSION_POLICY_CREATED, "Permission policy created: " + saved.code());
        return ExternalPortalPermissionPolicyResponse.from(saved);
    }
}
