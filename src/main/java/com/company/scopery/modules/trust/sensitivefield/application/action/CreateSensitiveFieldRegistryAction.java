package com.company.scopery.modules.trust.sensitivefield.application.action;
import com.company.scopery.modules.trust.sensitivefield.application.response.SensitiveFieldRegistryResponse;
import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistry;
import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistryRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSensitiveFieldRegistryAction {
    private final SensitiveFieldRegistryRepository repo;
    private final TrustAuthorizationService auth;
    public CreateSensitiveFieldRegistryAction(SensitiveFieldRegistryRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public SensitiveFieldRegistryResponse execute(UUID workspaceId, String objectTypeCode, String fieldPath,
            String classification, String maskingStrategy) {
        auth.requireManage(workspaceId);
        if (repo.existsByWorkspaceIdAndObjectTypeCodeAndFieldPath(workspaceId, objectTypeCode, fieldPath)) {
            throw TrustExceptions.sensitiveFieldAlreadyExists(fieldPath);
        }
        String resolvedClassification = classification != null ? classification : "CONFIDENTIAL";
        String resolvedMasking = maskingStrategy != null ? maskingStrategy : "REDACT";
        var saved = repo.save(SensitiveFieldRegistry.create(workspaceId, objectTypeCode, fieldPath, resolvedClassification, resolvedMasking));
        return SensitiveFieldRegistryResponse.from(saved);
    }
}
