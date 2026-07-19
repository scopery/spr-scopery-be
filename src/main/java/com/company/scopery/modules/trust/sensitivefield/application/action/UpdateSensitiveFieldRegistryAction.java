package com.company.scopery.modules.trust.sensitivefield.application.action;
import com.company.scopery.modules.trust.sensitivefield.application.response.SensitiveFieldRegistryResponse;
import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistryRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class UpdateSensitiveFieldRegistryAction {
    private final SensitiveFieldRegistryRepository repo;
    private final TrustAuthorizationService auth;
    public UpdateSensitiveFieldRegistryAction(SensitiveFieldRegistryRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public SensitiveFieldRegistryResponse execute(UUID workspaceId, UUID id, String classification,
            String maskingStrategy, Boolean exportAllowed, Boolean deactivate) {
        auth.requireManage(workspaceId);
        var existing = repo.findById(id).orElseThrow(() -> TrustExceptions.sensitiveFieldNotFound(id));
        if (Boolean.TRUE.equals(deactivate)) {
            return SensitiveFieldRegistryResponse.from(repo.save(existing.deactivate()));
        }
        String resolvedClass = classification != null ? classification : existing.classification();
        String resolvedMasking = maskingStrategy != null ? maskingStrategy : existing.maskingStrategy();
        boolean resolvedExport = exportAllowed != null ? exportAllowed : existing.exportAllowed();
        return SensitiveFieldRegistryResponse.from(repo.save(existing.update(resolvedClass, resolvedMasking, resolvedExport)));
    }
}
