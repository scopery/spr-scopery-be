package com.company.scopery.modules.collaboration.meetingseries.application.action;
import com.company.scopery.modules.collaboration.meetingseries.application.command.UpdateMeetingSeriesCommand;
import com.company.scopery.modules.collaboration.meetingseries.application.response.MeetingSeriesResponse;
import com.company.scopery.modules.collaboration.meetingseries.domain.enums.MeetingSeriesCadence;
import com.company.scopery.modules.collaboration.meetingseries.domain.model.*;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateMeetingSeriesAction {
    private final MeetingSeriesRepository series; private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;
    public UpdateMeetingSeriesAction(MeetingSeriesRepository series, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.series=series; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingSeriesResponse execute(UpdateMeetingSeriesCommand command) {
        authorization.requireSeriesManage(command.projectId());
        MeetingSeries s = series.findByIdAndProjectId(command.seriesId(), command.projectId())
                .orElseThrow(() -> CollaborationExceptions.seriesNotFound(command.seriesId()));
        if (command.title() == null || command.title().isBlank()) throw CollaborationExceptions.titleRequired();
        MeetingSeriesCadence cadence = CollaborationEnumParser.parseOptional(MeetingSeriesCadence.class, command.cadence(), "cadence");
        s = series.save(s.update(command.title().trim(), command.description(), cadence, command.ownerUserId(), Boolean.TRUE.equals(command.clientVisible())));
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING_SERIES, s.id(), CollaborationActivityActions.MEETING_SERIES_UPDATED, "Series updated");
        return MeetingSeriesResponse.from(s);
    }
}
