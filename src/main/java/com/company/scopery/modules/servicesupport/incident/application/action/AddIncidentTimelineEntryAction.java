package com.company.scopery.modules.servicesupport.incident.application.action;
import com.company.scopery.modules.servicesupport.incident.application.command.AddIncidentTimelineEntryCommand;
import com.company.scopery.modules.servicesupport.incident.application.response.IncidentTimelineEntryResponse;
import com.company.scopery.modules.servicesupport.incident.domain.model.IncidentTimelineEntry;
import com.company.scopery.modules.servicesupport.incident.domain.model.IncidentTimelineEntryRepository;
import com.company.scopery.modules.servicesupport.incident.domain.model.SupportIncidentRecordRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class AddIncidentTimelineEntryAction {
    private final IncidentTimelineEntryRepository timelines; private final SupportIncidentRecordRepository incidents; private final SupportAuthorizationService auth;
    public AddIncidentTimelineEntryAction(IncidentTimelineEntryRepository timelines, SupportIncidentRecordRepository incidents, SupportAuthorizationService auth){
        this.timelines=timelines; this.incidents=incidents; this.auth=auth;
    }
    @Transactional
    public IncidentTimelineEntryResponse execute(UUID workspaceId, UUID incidentId, AddIncidentTimelineEntryCommand cmd) {
        auth.requireManage(workspaceId);
        incidents.findById(incidentId).orElseThrow(() -> SupportExceptions.incidentNotFound(incidentId));
        String visibility = cmd.visibility() == null ? "INTERNAL" : cmd.visibility();
        var saved = timelines.save(IncidentTimelineEntry.create(workspaceId, incidentId, cmd.entryType(), visibility, cmd.message()));
        return IncidentTimelineEntryResponse.from(saved);
    }
}
