package com.company.scopery.modules.servicesupport.incident.application.action;
import com.company.scopery.modules.servicesupport.incident.application.command.ResolveIncidentCommand;
import com.company.scopery.modules.servicesupport.incident.application.response.SupportIncidentResponse;
import com.company.scopery.modules.servicesupport.incident.domain.model.SupportIncidentRecordRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ResolveIncidentAction {
    private final SupportIncidentRecordRepository repo; private final SupportAuthorizationService auth;
    public ResolveIncidentAction(SupportIncidentRecordRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public SupportIncidentResponse execute(UUID workspaceId, UUID incidentId, ResolveIncidentCommand cmd) {
        auth.requireManage(workspaceId);
        var incident = repo.findById(incidentId).orElseThrow(() -> SupportExceptions.incidentNotFound(incidentId));
        try { return SupportIncidentResponse.from(repo.save(incident.resolve(cmd.impactSummary()))); }
        catch (IllegalStateException ex) { throw SupportExceptions.incidentInvalidStatus(); }
    }
}
