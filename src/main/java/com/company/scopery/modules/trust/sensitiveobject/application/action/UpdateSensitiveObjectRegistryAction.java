package com.company.scopery.modules.trust.sensitiveobject.application.action;
import com.company.scopery.modules.trust.sensitiveobject.application.response.SensitiveObjectRegistryResponse;
import com.company.scopery.modules.trust.sensitiveobject.domain.model.SensitiveObjectRegistryRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class UpdateSensitiveObjectRegistryAction {
    private final SensitiveObjectRegistryRepository repo;
    private final TrustAuthorizationService auth;
    public UpdateSensitiveObjectRegistryAction(SensitiveObjectRegistryRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public SensitiveObjectRegistryResponse execute(UUID workspaceId, UUID id, String classification,
            Boolean exportReasonRequired, Boolean searchIndexAllowed, Boolean deactivate) {
        auth.requireManage(workspaceId);
        var existing = repo.findById(id).orElseThrow(() -> TrustExceptions.sensitiveObjectNotFound(id));
        if (Boolean.TRUE.equals(deactivate)) {
            return SensitiveObjectRegistryResponse.from(repo.save(existing.deactivate()));
        }
        String resolvedClass = classification != null ? classification : existing.classification();
        boolean resolvedExport = exportReasonRequired != null ? exportReasonRequired : existing.exportReasonRequired();
        boolean resolvedSearch = searchIndexAllowed != null ? searchIndexAllowed : existing.searchIndexAllowed();
        return SensitiveObjectRegistryResponse.from(repo.save(existing.update(resolvedClass, resolvedExport, resolvedSearch)));
    }
}
