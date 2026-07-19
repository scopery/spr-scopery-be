package com.company.scopery.modules.clientportal.policy.application.service;
import com.company.scopery.modules.clientportal.policy.application.response.ExternalPortalPermissionPolicyResponse;
import com.company.scopery.modules.clientportal.policy.domain.model.ExternalPortalPermissionPolicyRepository;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ExternalPortalPermissionPolicyQueryService {
    private final ExternalPortalPermissionPolicyRepository repo;
    public ExternalPortalPermissionPolicyQueryService(ExternalPortalPermissionPolicyRepository repo) { this.repo = repo; }
    @Transactional(readOnly = true)
    public List<ExternalPortalPermissionPolicyResponse> list(UUID workspaceId) {
        return repo.findByWorkspaceId(workspaceId).stream().map(ExternalPortalPermissionPolicyResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public ExternalPortalPermissionPolicyResponse get(UUID workspaceId, UUID policyId) {
        return ExternalPortalPermissionPolicyResponse.from(
                repo.findById(policyId).orElseThrow(() -> ClientPortalExceptions.permissionPolicyNotFound(policyId)));
    }
}
