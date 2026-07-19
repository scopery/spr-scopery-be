package com.company.scopery.modules.trust.sensitiveobject.application.action;
import com.company.scopery.modules.trust.sensitiveobject.application.response.SensitiveObjectRegistryResponse;
import com.company.scopery.modules.trust.sensitiveobject.domain.model.SensitiveObjectRegistry;
import com.company.scopery.modules.trust.sensitiveobject.domain.model.SensitiveObjectRegistryRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSensitiveObjectRegistryAction {
    private final SensitiveObjectRegistryRepository repo;
    private final TrustAuthorizationService auth;
    public CreateSensitiveObjectRegistryAction(SensitiveObjectRegistryRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public SensitiveObjectRegistryResponse execute(UUID workspaceId, String objectTypeCode, String classification) {
        auth.requireManage(workspaceId);
        if (repo.existsByWorkspaceIdAndObjectTypeCode(workspaceId, objectTypeCode)) {
            throw TrustExceptions.sensitiveObjectAlreadyExists(objectTypeCode);
        }
        String resolvedClassification = classification != null ? classification : "CONFIDENTIAL";
        var saved = repo.save(SensitiveObjectRegistry.create(workspaceId, objectTypeCode, resolvedClassification));
        return SensitiveObjectRegistryResponse.from(saved);
    }
}
