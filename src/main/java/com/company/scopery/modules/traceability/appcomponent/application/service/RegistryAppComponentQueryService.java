package com.company.scopery.modules.traceability.appcomponent.application.service;
import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.traceability.appcomponent.application.response.RegistryAppComponentResponse;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryAppComponentQueryService {
    private final RegistryAppComponentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final ObjectStorageProvider storageProvider;
    public RegistryAppComponentQueryService(RegistryAppComponentRepository repo,
                                             TraceabilityAuthorizationService authorization,
                                             ObjectStorageProvider storageProvider) {
        this.repo=repo; this.authorization=authorization; this.storageProvider=storageProvider;
    }
    @Transactional(readOnly=true)
    public List<RegistryAppComponentResponse> list(UUID applicationId) {
        return repo.findByApplicationId(applicationId).stream()
                .map(c -> RegistryAppComponentResponse.from(c, screenshotUrlFor(c.screenshotObjectKey())))
                .toList();
    }
    @Transactional(readOnly=true)
    public RegistryAppComponentResponse get(UUID workspaceId, UUID appComponentId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(appComponentId, workspaceId)
                .map(c -> RegistryAppComponentResponse.from(c, screenshotUrlFor(c.screenshotObjectKey())))
                .orElseThrow(() -> TraceabilityExceptions.appComponentNotFound(appComponentId));
    }
    private String screenshotUrlFor(String objectKey) {
        if (objectKey == null) return null;
        return storageProvider.createPresignedDownload(objectKey, null).downloadUrl();
    }
}
