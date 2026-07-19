package com.company.scopery.modules.trust.retention.application.service;
import com.company.scopery.modules.trust.retention.application.response.RetentionJobResponse;
import com.company.scopery.modules.trust.retention.application.response.RetentionPolicyResponse;
import com.company.scopery.modules.trust.retention.domain.model.RetentionJobRepository;
import com.company.scopery.modules.trust.retention.domain.model.RetentionPolicyRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RetentionQueryService {
    private final RetentionPolicyRepository policies;
    private final RetentionJobRepository jobs;
    private final TrustAuthorizationService auth;
    public RetentionQueryService(RetentionPolicyRepository policies, RetentionJobRepository jobs, TrustAuthorizationService auth) {
        this.policies = policies; this.jobs = jobs; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<RetentionPolicyResponse> listPolicies(UUID workspaceId) {
        auth.requireView(workspaceId);
        return policies.findByWorkspaceId(workspaceId).stream().map(RetentionPolicyResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public RetentionPolicyResponse getPolicy(UUID workspaceId, UUID policyId) {
        auth.requireView(workspaceId);
        return policies.findById(policyId).map(RetentionPolicyResponse::from)
                .orElseThrow(TrustExceptions::retentionPolicyNotFound);
    }
    @Transactional(readOnly = true)
    public List<RetentionJobResponse> listJobs(UUID workspaceId) {
        auth.requireView(workspaceId);
        return jobs.findByWorkspaceId(workspaceId).stream().map(RetentionJobResponse::from).toList();
    }
}
