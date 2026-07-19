package com.company.scopery.modules.trust.classification.application.service;
import com.company.scopery.modules.trust.classification.application.response.DataClassificationPolicyResponse;
import com.company.scopery.modules.trust.classification.domain.model.DataClassificationPolicyRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class DataClassificationPolicyQueryService {
    private final DataClassificationPolicyRepository repo;
    private final TrustAuthorizationService auth;
    public DataClassificationPolicyQueryService(DataClassificationPolicyRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public DataClassificationPolicyResponse getByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).map(DataClassificationPolicyResponse::from)
                .orElseThrow(() -> new com.company.scopery.common.exception.AppException(
                        com.company.scopery.modules.trust.shared.error.TrustErrorCatalog.DATA_CLASSIFICATION_POLICY_NOT_FOUND));
    }
}
