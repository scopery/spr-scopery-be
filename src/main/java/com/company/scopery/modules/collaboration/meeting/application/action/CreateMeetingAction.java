package com.company.scopery.modules.collaboration.meeting.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.collaboration.meeting.application.command.CreateMeetingCommand;
import com.company.scopery.modules.collaboration.meeting.application.response.MeetingResponse;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingType;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationActivityActions;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationEntityTypes;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateMeetingAction {
    private final ProjectRepository projects;
    private final MeetingRepository meetings;
    private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final CollaborationActivityLogger activityLogger;
    public CreateMeetingAction(ProjectRepository projects, MeetingRepository meetings,
                               CollaborationAuthorizationService authorization,
                               CurrentUserAuthorizationService currentUser,
                               CollaborationActivityLogger activityLogger) {
        this.projects = projects; this.meetings = meetings; this.authorization = authorization;
        this.currentUser = currentUser; this.activityLogger = activityLogger;
    }
    @Transactional
    public MeetingResponse execute(CreateMeetingCommand command) {
        authorization.requireMeetingCreate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw CollaborationExceptions.projectArchived(command.projectId());
        if (command.title() == null || command.title().isBlank()) throw CollaborationExceptions.titleRequired();
        MeetingType type = CollaborationEnumParser.parseRequired(MeetingType.class, command.meetingType(), "meetingType");
        var actor = currentUser.resolveCurrentUser();
        boolean clientVisible = Boolean.TRUE.equals(command.clientVisible());
        Meeting meeting = Meeting.create(project.workspaceId(), project.id(), command.meetingSeriesId(),
                command.title().trim(), command.description(), type, command.startAt(), command.endAt(),
                command.timezone(), command.location(), command.meetingUrl(),
                command.organizerUserId() != null ? command.organizerUserId() : actor.id(), clientVisible);
        meeting = meetings.save(meeting);
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING, meeting.id(),
                CollaborationActivityActions.MEETING_CREATED, "Meeting created: " + meeting.title());
        return MeetingResponse.from(meeting);
    }
}
