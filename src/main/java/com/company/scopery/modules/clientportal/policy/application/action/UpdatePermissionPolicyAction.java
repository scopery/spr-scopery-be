package com.company.scopery.modules.clientportal.policy.application.action;
import com.company.scopery.modules.clientportal.policy.application.command.UpdatePermissionPolicyCommand;
import com.company.scopery.modules.clientportal.policy.application.response.ExternalPortalPermissionPolicyResponse;
import com.company.scopery.modules.clientportal.policy.domain.model.ExternalPortalPermissionPolicyRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdatePermissionPolicyAction {
    private final ExternalPortalPermissionPolicyRepository repo;
    private final ClientPortalActivityLogger activityLogger;
    public UpdatePermissionPolicyAction(ExternalPortalPermissionPolicyRepository repo, ClientPortalActivityLogger activityLogger) {
        this.repo = repo; this.activityLogger = activityLogger;
    }
    @Transactional
    public ExternalPortalPermissionPolicyResponse execute(UpdatePermissionPolicyCommand c) {
        var policy = repo.findById(c.policyId()).orElseThrow(() -> ClientPortalExceptions.permissionPolicyNotFound(c.policyId()));
        var saved = repo.save(policy.update(c.name(), c.description(), c.permissionsJson()));
        activityLogger.logSuccess(ClientPortalEntityTypes.PERMISSION_POLICY, saved.id(), ClientPortalActivityActions.PERMISSION_POLICY_UPDATED, "Permission policy updated: " + saved.code());
        return ExternalPortalPermissionPolicyResponse.from(saved);
    }
}
