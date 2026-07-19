package com.company.scopery.modules.documenthub.comment.application.action;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.comment.application.command.OpenCommentThreadCommand;
import com.company.scopery.modules.documenthub.comment.application.response.CommentResponse;
import com.company.scopery.modules.documenthub.comment.application.response.CommentThreadResponse;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentComment;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentRepository;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThread;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThreadRepository;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubOutboxEventCodes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class OpenCommentThreadAction {

    private final DocumentRepository documents;
    private final DocumentCommentThreadRepository threadRepo;
    private final DocumentCommentRepository commentRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    private final TransactionalOutboxService outbox;

    public OpenCommentThreadAction(DocumentRepository documents,
                                    DocumentCommentThreadRepository threadRepo,
                                    DocumentCommentRepository commentRepo,
                                    DocumentHubAuthorizationService authorization,
                                    DocumentHubActivityLogger activityLogger,
                                    TransactionalOutboxService outbox) {
        this.documents = documents;
        this.threadRepo = threadRepo;
        this.commentRepo = commentRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.outbox = outbox;
    }

    @Transactional
    public CommentThreadResponse execute(OpenCommentThreadCommand c) {
        authorization.requireUpdate(c.projectId());

        var document = documents.findByIdAndProjectId(c.documentId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(c.documentId()));

        DocumentCommentThread thread = threadRepo.save(
                DocumentCommentThread.create(c.documentId(), document.workspaceId(), c.projectId(),
                        c.blockId(), c.anchorText()));

        DocumentComment firstComment = commentRepo.save(
                DocumentComment.create(thread.id(), c.documentId(), c.firstCommentBody()));

        outbox.enqueue(DocumentHubEntityTypes.COMMENT_THREAD, thread.id(),
                DocumentHubOutboxEventCodes.DOCUMENT_COMMENT_THREAD_OPENED,
                Map.of("documentId", c.documentId(), "threadId", thread.id()));

        activityLogger.logSuccess(DocumentHubEntityTypes.COMMENT_THREAD, thread.id(),
                DocumentHubActivityActions.COMMENT_THREAD_OPENED, "Comment thread opened on block " + c.blockId());

        return CommentThreadResponse.from(thread, List.of(CommentResponse.from(firstComment)));
    }
}
