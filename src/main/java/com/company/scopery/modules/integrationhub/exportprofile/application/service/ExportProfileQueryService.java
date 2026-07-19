package com.company.scopery.modules.integrationhub.exportprofile.application.service;
import com.company.scopery.modules.integrationhub.exportprofile.application.response.ExportProfileResponse;
import com.company.scopery.modules.integrationhub.exportprofile.domain.model.ExportProfileRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ExportProfileQueryService {
    private final ExportProfileRepository repo;
    private final IntegrationAuthorizationService auth;
    public ExportProfileQueryService(ExportProfileRepository repo, IntegrationAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<ExportProfileResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ExportProfileResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public ExportProfileResponse getById(UUID workspaceId, UUID profileId) {
        auth.requireView(workspaceId);
        var p = repo.findById(profileId).orElseThrow(() -> IntegrationExceptions.exportProfileNotFound(profileId));
        if (!workspaceId.equals(p.workspaceId())) throw IntegrationExceptions.exportProfileNotFound(profileId);
        return ExportProfileResponse.from(p);
    }
}
