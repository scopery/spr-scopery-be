package com.company.scopery.modules.documenthub.comment.application.action;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.comment.application.command.ResolveCommentThreadCommand;
import com.company.scopery.modules.documenthub.comment.application.response.CommentThreadResponse;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThreadRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubOutboxEventCodes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component("documentHubResolveCommentThreadAction")
public class ResolveCommentThreadAction {

    private final DocumentCommentThreadRepository threadRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    private final TransactionalOutboxService outbox;

    public ResolveCommentThreadAction(DocumentCommentThreadRepository threadRepo,
                                       DocumentHubAuthorizationService authorization,
                                       DocumentHubActivityLogger activityLogger,
                                       TransactionalOutboxService outbox) {
        this.threadRepo = threadRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.outbox = outbox;
    }

    @Transactional
    public CommentThreadResponse execute(ResolveCommentThreadCommand c) {
        authorization.requireUpdate(c.projectId());

        var thread = threadRepo.findByIdAndDocumentId(c.threadId(), c.documentId())
                .orElseThrow(() -> DocumentHubExceptions.commentThreadNotFound(c.threadId()));

        UUID actorId = resolveActorId();
        var resolved = threadRepo.save(thread.resolve(actorId));

        outbox.enqueue(DocumentHubEntityTypes.COMMENT_THREAD, resolved.id(),
                DocumentHubOutboxEventCodes.DOCUMENT_COMMENT_THREAD_RESOLVED,
                Map.of("documentId", c.documentId(), "threadId", resolved.id()));

        activityLogger.logSuccess(DocumentHubEntityTypes.COMMENT_THREAD, resolved.id(),
                DocumentHubActivityActions.COMMENT_THREAD_RESOLVED, "Comment thread resolved");

        return CommentThreadResponse.from(resolved);
    }

    private UUID resolveActorId() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof String userId) {
                return UUID.fromString(userId);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
