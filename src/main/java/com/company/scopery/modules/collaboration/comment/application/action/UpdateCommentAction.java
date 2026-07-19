package com.company.scopery.modules.collaboration.comment.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.comment.application.response.CommentResponse;
import com.company.scopery.modules.collaboration.comment.domain.model.CommentRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.comment.application.command.UpdateCommentCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateCommentAction {
    private final CommentRepository comments; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public UpdateCommentAction(CommentRepository comments, CollaborationAuthorizationService authorization, CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.comments=comments; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public CommentResponse execute(UpdateCommentCommand c) {
        authorization.requireCommentUpdate(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var comment = comments.findByIdAndProjectId(c.commentId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.commentNotFound(c.commentId()));
        comment = comments.save(comment.update(c.body(), actor.id()));
        activityLogger.logSuccess(CollaborationEntityTypes.COMMENT, comment.id(), CollaborationActivityActions.COMMENT_UPDATED, "Comment updated");
        return CommentResponse.from(comment);
    }
}
