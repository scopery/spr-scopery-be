package com.company.scopery.modules.collaboration.meetingseries.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.collaboration.meetingseries.application.command.CreateMeetingSeriesCommand;
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
public class CreateMeetingSeriesAction {
    private final ProjectRepository projects; private final MeetingSeriesRepository series;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public CreateMeetingSeriesAction(ProjectRepository projects, MeetingSeriesRepository series,
                                     CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.projects=projects; this.series=series; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingSeriesResponse execute(CreateMeetingSeriesCommand command) {
        authorization.requireSeriesManage(command.projectId());
        Project project = projects.findById(command.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw CollaborationExceptions.projectArchived(command.projectId());
        if (command.title() == null || command.title().isBlank()) throw CollaborationExceptions.titleRequired();
        MeetingSeriesCadence cadence = CollaborationEnumParser.parseOptional(MeetingSeriesCadence.class, command.cadence(), "cadence");
        MeetingSeries s = MeetingSeries.create(project.workspaceId(), project.id(), command.code(), command.title().trim(),
                command.description(), cadence, command.ownerUserId(), Boolean.TRUE.equals(command.clientVisible()));
        s = series.save(s);
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING_SERIES, s.id(), CollaborationActivityActions.MEETING_SERIES_CREATED, "Series created");
        return MeetingSeriesResponse.from(s);
    }
}
