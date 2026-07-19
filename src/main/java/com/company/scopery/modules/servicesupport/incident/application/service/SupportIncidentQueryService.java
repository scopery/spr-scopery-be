package com.company.scopery.modules.servicesupport.incident.application.service;
import com.company.scopery.modules.servicesupport.incident.application.response.IncidentTimelineEntryResponse;
import com.company.scopery.modules.servicesupport.incident.application.response.SupportIncidentResponse;
import com.company.scopery.modules.servicesupport.incident.domain.model.IncidentTimelineEntryRepository;
import com.company.scopery.modules.servicesupport.incident.domain.model.SupportIncidentRecordRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportIncidentQueryService {
    private final SupportIncidentRecordRepository incidents; private final IncidentTimelineEntryRepository timeline;
    private final SupportAuthorizationService auth;
    public SupportIncidentQueryService(SupportIncidentRecordRepository incidents, IncidentTimelineEntryRepository timeline, SupportAuthorizationService auth){
        this.incidents=incidents; this.timeline=timeline; this.auth=auth;
    }
    public List<SupportIncidentResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return incidents.findByWorkspaceId(workspaceId).stream().map(SupportIncidentResponse::from).toList();
    }
    public List<IncidentTimelineEntryResponse> listTimeline(UUID workspaceId, UUID incidentId) {
        auth.requireView(workspaceId);
        return timeline.findByIncidentId(incidentId).stream().map(IncidentTimelineEntryResponse::from).toList();
    }
}
