package com.company.scopery.modules.integrationhub.importtemplate.application.service;
import com.company.scopery.modules.integrationhub.importtemplate.application.response.ImportTemplateResponse;
import com.company.scopery.modules.integrationhub.importtemplate.domain.model.ImportTemplateRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ImportTemplateQueryService {
    private final ImportTemplateRepository repo;
    private final IntegrationAuthorizationService auth;
    public ImportTemplateQueryService(ImportTemplateRepository repo, IntegrationAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<ImportTemplateResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceIdOrGlobal(workspaceId).stream().map(ImportTemplateResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public ImportTemplateResponse getById(UUID workspaceId, UUID templateId) {
        auth.requireView(workspaceId);
        return repo.findById(templateId).map(ImportTemplateResponse::from)
                .orElseThrow(() -> IntegrationExceptions.importTemplateNotFound(templateId));
    }
}
