package com.company.scopery.modules.servicesupport.warranty.application.action;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.warranty.application.command.CreateWarrantyCoverageCommand;
import com.company.scopery.modules.servicesupport.warranty.application.response.WarrantyCoverageResponse;
import com.company.scopery.modules.servicesupport.warranty.domain.model.WarrantyCoverage;
import com.company.scopery.modules.servicesupport.warranty.domain.model.WarrantyCoverageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate; import java.util.UUID;
@Component
public class CreateWarrantyCoverageAction {
    private final WarrantyCoverageRepository repo; private final SupportAuthorizationService auth;
    private final SupportActivityLogger activity;
    public CreateWarrantyCoverageAction(WarrantyCoverageRepository repo, SupportAuthorizationService auth, SupportActivityLogger activity){
        this.repo=repo; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public WarrantyCoverageResponse execute(UUID workspaceId, CreateWarrantyCoverageCommand cmd) {
        auth.requireManage(workspaceId);
        LocalDate start = cmd.startDate() == null ? LocalDate.now() : cmd.startDate();
        WarrantyCoverage saved = repo.save(WarrantyCoverage.create(workspaceId, cmd.projectId(), cmd.serviceProfileId(), start, cmd.endDate()));
        activity.logSuccess("WARRANTY_COVERAGE", saved.id(), "WARRANTY_CREATED", "Warranty coverage created");
        return WarrantyCoverageResponse.from(saved);
    }
}
