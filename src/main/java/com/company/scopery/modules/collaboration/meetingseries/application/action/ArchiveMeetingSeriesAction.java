package com.company.scopery.modules.collaboration.meetingseries.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.meetingseries.application.response.MeetingSeriesResponse;
import com.company.scopery.modules.collaboration.meetingseries.domain.model.*;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.meetingseries.application.command.ArchiveMeetingSeriesCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ArchiveMeetingSeriesAction {
    private final MeetingSeriesRepository series; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public ArchiveMeetingSeriesAction(MeetingSeriesRepository series, CollaborationAuthorizationService authorization,
                                      CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.series=series; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingSeriesResponse execute(ArchiveMeetingSeriesCommand c) {
        authorization.requireSeriesManage(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        MeetingSeries s = series.findByIdAndProjectId(c.seriesId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.seriesNotFound(c.seriesId()));
        s = series.save(s.archive(actor.id()));
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING_SERIES, s.id(), CollaborationActivityActions.MEETING_SERIES_ARCHIVED, "Series archived");
        return MeetingSeriesResponse.from(s);
    }
}
