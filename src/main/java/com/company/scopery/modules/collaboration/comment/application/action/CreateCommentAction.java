package com.company.scopery.modules.collaboration.comment.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.comment.application.response.CommentResponse;
import com.company.scopery.modules.collaboration.comment.domain.enums.CommentAuthorType;
import com.company.scopery.modules.collaboration.comment.domain.model.*;
import com.company.scopery.modules.collaboration.commentthread.domain.model.CommentThreadRepository;
import com.company.scopery.modules.collaboration.mention.domain.enums.*;
import com.company.scopery.modules.collaboration.mention.domain.model.*;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.comment.application.command.CreateCommentCommand;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateCommentAction {
    private final CommentThreadRepository threads; private final CommentRepository comments; private final MentionRepository mentions;
    private final CollaborationAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    private final CollaborationActivityLogger activityLogger;
    public CreateCommentAction(CommentThreadRepository threads, CommentRepository comments, MentionRepository mentions,
                               CollaborationAuthorizationService authorization, CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.threads=threads; this.comments=comments; this.mentions=mentions; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public CommentResponse execute(CreateCommentCommand cmd) {
        authorization.requireCommentCreate(cmd.projectId());
        var actor = currentUser.resolveCurrentUser();
        var thread = threads.findByIdAndProjectId(cmd.threadId(), cmd.projectId()).orElseThrow(() -> CollaborationExceptions.threadNotFound(cmd.threadId()));
        var c = Comment.create(thread.workspaceId(), thread.projectId(), thread.id(), cmd.parentId(), CommentAuthorType.INTERNAL_USER,
                actor.id(), actor.username() == null ? null : actor.username().value(), cmd.body(), Boolean.TRUE.equals(cmd.clientVisible()));
        c = comments.save(c);
        if (cmd.mentionUserIds() != null) {
            for (UUID uid : cmd.mentionUserIds()) {
                var m = Mention.create(thread.workspaceId(), thread.projectId(), MentionSourceType.COMMENT, c.id(), MentionTargetType.INTERNAL_USER, uid);
                mentions.save(m);
                activityLogger.logSuccess(CollaborationEntityTypes.MENTION, m.id(), CollaborationActivityActions.MENTION_CREATED, "Mention created");
            }
        }
        activityLogger.logSuccess(CollaborationEntityTypes.COMMENT, c.id(), CollaborationActivityActions.COMMENT_CREATED, "Comment created");
        return CommentResponse.from(c);
    }
}
