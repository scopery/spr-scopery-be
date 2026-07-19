package com.company.scopery.modules.traceability.screensection.application.service;
import com.company.scopery.modules.traceability.screensection.application.response.RegistryScreenSectionResponse;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSectionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryScreenSectionQueryService {
    private final RegistryScreenSectionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public RegistryScreenSectionQueryService(RegistryScreenSectionRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RegistryScreenSectionResponse> list(UUID workspaceId, UUID screenId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByScreenId(screenId).stream().map(RegistryScreenSectionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RegistryScreenSectionResponse get(UUID workspaceId, UUID sectionId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(sectionId, workspaceId).map(RegistryScreenSectionResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.screenSectionNotFound(sectionId));
    }
}
