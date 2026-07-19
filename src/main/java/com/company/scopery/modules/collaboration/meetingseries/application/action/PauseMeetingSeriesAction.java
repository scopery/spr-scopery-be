package com.company.scopery.modules.collaboration.meetingseries.application.action;
import com.company.scopery.modules.collaboration.meetingseries.application.response.MeetingSeriesResponse;
import com.company.scopery.modules.collaboration.meetingseries.domain.model.*;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.meetingseries.application.command.PauseMeetingSeriesCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class PauseMeetingSeriesAction {
    private final MeetingSeriesRepository series; private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;
    public PauseMeetingSeriesAction(MeetingSeriesRepository series, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.series=series; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingSeriesResponse execute(PauseMeetingSeriesCommand c) {
        authorization.requireSeriesManage(c.projectId());
        MeetingSeries s = series.findByIdAndProjectId(c.seriesId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.seriesNotFound(c.seriesId()));
        s = series.save(s.pause());
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING_SERIES, s.id(), CollaborationActivityActions.MEETING_SERIES_PAUSED, "Series paused");
        return MeetingSeriesResponse.from(s);
    }
}
