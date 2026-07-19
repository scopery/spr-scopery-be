package com.company.scopery.modules.trust.consent.application.service;
import com.company.scopery.modules.trust.consent.application.response.ContactSuppressionResponse;
import com.company.scopery.modules.trust.consent.domain.model.ContactSuppressionRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ContactSuppressionQueryService {
    private final ContactSuppressionRepository repo;
    private final TrustAuthorizationService auth;
    public ContactSuppressionQueryService(ContactSuppressionRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<ContactSuppressionResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ContactSuppressionResponse::from).toList();
    }
}
