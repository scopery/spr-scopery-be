package com.company.scopery.modules.collaboration.commentthread.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.commentthread.application.response.CommentThreadResponse;
import com.company.scopery.modules.collaboration.commentthread.domain.model.CommentThreadRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.commentthread.application.command.ResolveCommentThreadCommand;
import org.springframework.transaction.annotation.Transactional;
@Component("collaborationResolveCommentThreadAction")
public class ResolveCommentThreadAction {
    private final CommentThreadRepository threads; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public ResolveCommentThreadAction(CommentThreadRepository threads, CollaborationAuthorizationService authorization, CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.threads=threads; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public CommentThreadResponse execute(ResolveCommentThreadCommand c) {
        authorization.requireCommentThreadCreate(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var t = threads.findByIdAndProjectId(c.threadId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.threadNotFound(c.threadId()));
        t = threads.save(t.resolve(actor.id()));
        activityLogger.logSuccess(CollaborationEntityTypes.COMMENT_THREAD, t.id(), CollaborationActivityActions.THREAD_RESOLVED, "Thread resolved");
        return CommentThreadResponse.from(t);
    }
}
