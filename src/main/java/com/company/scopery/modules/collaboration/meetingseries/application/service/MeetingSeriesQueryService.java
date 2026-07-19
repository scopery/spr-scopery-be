package com.company.scopery.modules.collaboration.meetingseries.application.service;
import com.company.scopery.modules.collaboration.meetingseries.application.response.MeetingSeriesResponse;
import com.company.scopery.modules.collaboration.meetingseries.domain.model.MeetingSeriesRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class MeetingSeriesQueryService {
    private final MeetingSeriesRepository series; private final CollaborationAuthorizationService authorization;
    public MeetingSeriesQueryService(MeetingSeriesRepository series, CollaborationAuthorizationService authorization) {
        this.series=series; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public MeetingSeriesResponse get(UUID projectId, UUID seriesId) {
        authorization.requireSeriesManage(projectId);
        return MeetingSeriesResponse.from(series.findByIdAndProjectId(seriesId, projectId)
                .orElseThrow(() -> CollaborationExceptions.seriesNotFound(seriesId)));
    }
    @Transactional(readOnly=true)
    public List<MeetingSeriesResponse> list(UUID projectId) {
        authorization.requireSeriesManage(projectId);
        return series.findByProjectId(projectId).stream().map(MeetingSeriesResponse::from).toList();
    }
}
