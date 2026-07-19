package com.company.scopery.modules.trust.classification.application.action;
import com.company.scopery.modules.trust.classification.application.response.DataClassificationPolicyResponse;
import com.company.scopery.modules.trust.classification.domain.model.DataClassificationPolicy;
import com.company.scopery.modules.trust.classification.domain.model.DataClassificationPolicyRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class UpsertDataClassificationPolicyAction {
    private final DataClassificationPolicyRepository repo;
    private final TrustAuthorizationService auth;
    public UpsertDataClassificationPolicyAction(DataClassificationPolicyRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public DataClassificationPolicyResponse execute(UUID workspaceId, String policyCode, String name,
            String defaultClassification, String description) {
        auth.requireManage(workspaceId);
        var existing = repo.findByWorkspaceId(workspaceId);
        DataClassificationPolicy policy;
        if (existing.isPresent()) {
            policy = existing.get().withDefault(defaultClassification);
        } else {
            policy = DataClassificationPolicy.create(workspaceId, policyCode, name, defaultClassification);
        }
        return DataClassificationPolicyResponse.from(repo.save(policy));
    }
}
