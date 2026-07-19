package com.company.scopery.modules.trust.consent.application.service;
import com.company.scopery.modules.trust.consent.application.response.ConsentRecordResponse;
import com.company.scopery.modules.trust.consent.domain.model.ConsentRecordRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ConsentQueryService {
    private final ConsentRecordRepository repo;
    private final TrustAuthorizationService auth;
    public ConsentQueryService(ConsentRecordRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<ConsentRecordResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ConsentRecordResponse::from).toList();
    }
}
