package com.company.scopery.modules.trust.legalhold.application.service;
import com.company.scopery.modules.trust.legalhold.application.response.LegalHoldResponse;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHoldRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class LegalHoldQueryService {
    private final LegalHoldRepository repo;
    private final TrustAuthorizationService auth;
    public LegalHoldQueryService(LegalHoldRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<LegalHoldResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(LegalHoldResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public LegalHoldResponse getById(UUID workspaceId, UUID holdId) {
        auth.requireView(workspaceId);
        return repo.findById(holdId).map(LegalHoldResponse::from).orElseThrow(() -> TrustExceptions.legalHoldNotFound(holdId));
    }
}
