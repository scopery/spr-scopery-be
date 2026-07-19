package com.company.scopery.modules.documenthub.comment.application.action;

import com.company.scopery.modules.documenthub.comment.application.command.DeleteCommentCommand;
import com.company.scopery.modules.documenthub.comment.application.response.CommentResponse;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("documentHubDeleteCommentAction")
public class DeleteCommentAction {

    private final DocumentCommentRepository commentRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;

    public DeleteCommentAction(DocumentCommentRepository commentRepo,
                                DocumentHubAuthorizationService authorization,
                                DocumentHubActivityLogger activityLogger) {
        this.commentRepo = commentRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CommentResponse execute(DeleteCommentCommand c) {
        authorization.requireUpdate(c.projectId());

        var comment = commentRepo.findById(c.commentId())
                .orElseThrow(() -> DocumentHubExceptions.commentNotFound(c.commentId()));

        var deleted = commentRepo.save(comment.softDelete());

        activityLogger.logSuccess(DocumentHubEntityTypes.COMMENT, deleted.id(),
                DocumentHubActivityActions.COMMENT_DELETED, "Comment soft-deleted");

        return CommentResponse.from(deleted);
    }
}
