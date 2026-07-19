package com.company.scopery.modules.collaboration.commentthread.application.action;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.collaboration.commentthread.application.response.CommentThreadResponse;
import com.company.scopery.modules.collaboration.commentthread.domain.model.*;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.commentthread.application.command.CreateCommentThreadCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateCommentThreadAction {
    private final ProjectRepository projects; private final CommentThreadRepository threads;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public CreateCommentThreadAction(ProjectRepository projects, CommentThreadRepository threads, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.projects=projects; this.threads=threads; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public CommentThreadResponse execute(CreateCommentThreadCommand c) {
        authorization.requireCommentThreadCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        var t = CommentThread.create(project.workspaceId(), project.id(), c.targetType(), c.targetId(), c.title(), Boolean.TRUE.equals(c.clientVisible()));
        t = threads.save(t);
        activityLogger.logSuccess(CollaborationEntityTypes.COMMENT_THREAD, t.id(), CollaborationActivityActions.THREAD_CREATED, "Thread created");
        return CommentThreadResponse.from(t);
    }
}
