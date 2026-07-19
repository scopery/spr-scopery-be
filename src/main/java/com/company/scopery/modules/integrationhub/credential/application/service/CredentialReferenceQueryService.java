package com.company.scopery.modules.integrationhub.credential.application.service;
import com.company.scopery.modules.integrationhub.credential.application.response.CredentialReferenceResponse;
import com.company.scopery.modules.integrationhub.credential.domain.model.CredentialReferenceRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class CredentialReferenceQueryService {
    private final CredentialReferenceRepository repo;
    private final IntegrationAuthorizationService auth;
    public CredentialReferenceQueryService(CredentialReferenceRepository repo, IntegrationAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<CredentialReferenceResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(CredentialReferenceResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public CredentialReferenceResponse getById(UUID workspaceId, UUID credId) {
        auth.requireView(workspaceId);
        var c = repo.findById(credId).orElseThrow(() -> IntegrationExceptions.credentialNotFound(credId));
        if (!workspaceId.equals(c.workspaceId())) throw IntegrationExceptions.credentialNotFound(credId);
        return CredentialReferenceResponse.from(c);
    }
}
