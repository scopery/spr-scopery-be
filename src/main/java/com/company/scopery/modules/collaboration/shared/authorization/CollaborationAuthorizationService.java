package com.company.scopery.modules.collaboration.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class CollaborationAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public CollaborationAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.projectAuthorization = projectAuthorization;
    }
    public void requireMeetingView(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_VIEW); }
    public void requireMeetingCreate(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_CREATE); }
    public void requireMeetingUpdate(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_UPDATE); }
    public void requireMeetingCancel(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_CANCEL); }
    public void requireMeetingArchive(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_ARCHIVE); }
    public void requireSeriesManage(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_SERIES_MANAGE); }
    public void requireParticipantManage(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_PARTICIPANT_MANAGE); }
    public void requireAgendaManage(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_AGENDA_MANAGE); }
    public void requireMinutesView(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_MINUTES_VIEW); }
    public void requireMinutesUpdate(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_MINUTES_UPDATE); }
    public void requireMinutesApprove(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_MINUTES_APPROVE); }
    public void requireMinutesGenerateDocument(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_MINUTES_GENERATE_DOCUMENT); }
    public void requireNoteManage(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_NOTE_UPDATE); }
    public void requireActionManage(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_ACTION_UPDATE); }
    public void requireActionComplete(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_ACTION_COMPLETE); }
    public void requireActionCreateTask(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_ACTION_CREATE_TASK); }
    public void requireLinkManage(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_LINK_MANAGE); }
    public void requireCommentThreadView(UUID projectId) { require(projectId, IamAuthorities.PROJECT_COMMENT_THREAD_VIEW); }
    public void requireCommentThreadCreate(UUID projectId) { require(projectId, IamAuthorities.PROJECT_COMMENT_THREAD_CREATE); }
    public void requireCommentCreate(UUID projectId) { require(projectId, IamAuthorities.PROJECT_COMMENT_CREATE); }
    public void requireCommentUpdate(UUID projectId) { require(projectId, IamAuthorities.PROJECT_COMMENT_UPDATE); }
    public void requireReportView(UUID projectId) { require(projectId, IamAuthorities.PROJECT_MEETING_REPORT_VIEW); }
    private void require(UUID projectId, IamPermissionAction a) {
        try { projectAuthorization.requireProjectPermission(projectId, a); }
        catch (RuntimeException ex) { throw CollaborationExceptions.accessDenied(); }
    }
}
