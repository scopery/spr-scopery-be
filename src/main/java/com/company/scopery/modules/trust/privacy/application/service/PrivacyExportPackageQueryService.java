package com.company.scopery.modules.trust.privacy.application.service;
import com.company.scopery.modules.trust.privacy.application.response.PrivacyExportPackageResponse;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyExportPackageRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class PrivacyExportPackageQueryService {
    private final PrivacyExportPackageRepository repo;
    private final TrustAuthorizationService auth;
    public PrivacyExportPackageQueryService(PrivacyExportPackageRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<PrivacyExportPackageResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(PrivacyExportPackageResponse::from).toList();
    }
}
