package com.company.scopery.modules.servicesupport.handover.application.action;
import com.company.scopery.modules.servicesupport.handover.application.response.ServiceHandoverPackageResponse;
import com.company.scopery.modules.servicesupport.handover.domain.model.ServiceHandoverPackageRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class FinalizeHandoverPackageAction {
    private final ServiceHandoverPackageRepository repo; private final SupportAuthorizationService auth;
    public FinalizeHandoverPackageAction(ServiceHandoverPackageRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public ServiceHandoverPackageResponse execute(UUID workspaceId, UUID packageId, UUID finalizedBy) {
        auth.requireManage(workspaceId);
        var pkg = repo.findById(packageId).orElseThrow(() -> SupportExceptions.handoverPackageNotFound(packageId));
        try { return ServiceHandoverPackageResponse.from(repo.save(pkg.finalizePackage(finalizedBy))); }
        catch (IllegalStateException ex) { throw SupportExceptions.invalidStatus(); }
    }
}
