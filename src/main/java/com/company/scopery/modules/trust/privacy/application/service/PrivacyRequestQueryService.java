package com.company.scopery.modules.trust.privacy.application.service;
import com.company.scopery.modules.trust.privacy.application.response.PrivacyRequestResponse;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyRequestRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class PrivacyRequestQueryService {
    private final PrivacyRequestRepository repo;
    private final TrustAuthorizationService auth;
    public PrivacyRequestQueryService(PrivacyRequestRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<PrivacyRequestResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(PrivacyRequestResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public PrivacyRequestResponse getById(UUID workspaceId, UUID requestId) {
        auth.requireView(workspaceId);
        return repo.findById(requestId).map(PrivacyRequestResponse::from)
                .orElseThrow(() -> TrustExceptions.privacyNotFound(requestId));
    }
}
