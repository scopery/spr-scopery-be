package com.company.scopery.modules.documenthub.comment.application.action;

import com.company.scopery.modules.documenthub.comment.application.command.AddCommentCommand;
import com.company.scopery.modules.documenthub.comment.application.response.CommentResponse;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentComment;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentRepository;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThreadRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddCommentAction {

    private final DocumentCommentThreadRepository threadRepo;
    private final DocumentCommentRepository commentRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;

    public AddCommentAction(DocumentCommentThreadRepository threadRepo,
                             DocumentCommentRepository commentRepo,
                             DocumentHubAuthorizationService authorization,
                             DocumentHubActivityLogger activityLogger) {
        this.threadRepo = threadRepo;
        this.commentRepo = commentRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CommentResponse execute(AddCommentCommand c) {
        authorization.requireUpdate(c.projectId());

        var thread = threadRepo.findByIdAndDocumentId(c.threadId(), c.documentId())
                .orElseThrow(() -> DocumentHubExceptions.commentThreadNotFound(c.threadId()));

        DocumentComment saved = commentRepo.save(DocumentComment.create(thread.id(), c.documentId(), c.body()));

        activityLogger.logSuccess(DocumentHubEntityTypes.COMMENT, saved.id(),
                DocumentHubActivityActions.COMMENT_ADDED, "Comment added to thread " + c.threadId());

        return CommentResponse.from(saved);
    }
}
