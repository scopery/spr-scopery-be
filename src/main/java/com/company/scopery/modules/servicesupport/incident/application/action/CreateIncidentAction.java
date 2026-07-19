package com.company.scopery.modules.servicesupport.incident.application.action;
import com.company.scopery.modules.servicesupport.incident.application.command.CreateIncidentCommand;
import com.company.scopery.modules.servicesupport.incident.application.response.SupportIncidentResponse;
import com.company.scopery.modules.servicesupport.incident.domain.model.SupportIncidentRecord;
import com.company.scopery.modules.servicesupport.incident.domain.model.SupportIncidentRecordRepository;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateIncidentAction {
    private final SupportIncidentRecordRepository repo; private final SupportAuthorizationService auth; private final SupportActivityLogger activity;
    public CreateIncidentAction(SupportIncidentRecordRepository repo, SupportAuthorizationService auth, SupportActivityLogger activity){ this.repo=repo; this.auth=auth; this.activity=activity; }
    @Transactional
    public SupportIncidentResponse execute(UUID workspaceId, CreateIncidentCommand cmd) {
        auth.requireManage(workspaceId);
        var saved = repo.save(SupportIncidentRecord.create(workspaceId, cmd.projectId(), cmd.title(), cmd.severity()));
        activity.logSuccess("INCIDENT", saved.id(), "INCIDENT_CREATED", "Incident created: " + saved.incidentNumber());
        return SupportIncidentResponse.from(saved);
    }
}
