package com.company.scopery.modules.servicesupport.warranty.application.action;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import com.company.scopery.modules.servicesupport.warranty.application.response.WarrantyCoverageResponse;
import com.company.scopery.modules.servicesupport.warranty.domain.model.WarrantyCoverageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ExpireWarrantyCoverageAction {
    private final WarrantyCoverageRepository repo; private final SupportAuthorizationService auth; private final SupportActivityLogger activity;
    public ExpireWarrantyCoverageAction(WarrantyCoverageRepository repo, SupportAuthorizationService auth, SupportActivityLogger activity){ this.repo=repo; this.auth=auth; this.activity=activity; }
    @Transactional
    public WarrantyCoverageResponse execute(UUID workspaceId, UUID warrantyId) {
        auth.requireManage(workspaceId);
        var coverage = repo.findById(warrantyId).orElseThrow(() -> SupportExceptions.warrantyNotFound(warrantyId));
        var saved = repo.save(coverage.expire());
        activity.logSuccess("WARRANTY_COVERAGE", saved.id(), "WARRANTY_EXPIRED", "Warranty coverage expired");
        return WarrantyCoverageResponse.from(saved);
    }
}
