package com.company.scopery.modules.documenthub.comment.application.service;

import com.company.scopery.modules.documenthub.comment.application.response.CommentResponse;
import com.company.scopery.modules.documenthub.comment.application.response.CommentThreadResponse;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentRepository;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThreadRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service("documentHubCommentThreadQueryService")
public class CommentThreadQueryService {

    private final DocumentCommentThreadRepository threadRepo;
    private final DocumentCommentRepository commentRepo;
    private final DocumentHubAuthorizationService authorization;

    public CommentThreadQueryService(DocumentCommentThreadRepository threadRepo,
                                      DocumentCommentRepository commentRepo,
                                      DocumentHubAuthorizationService authorization) {
        this.threadRepo = threadRepo;
        this.commentRepo = commentRepo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<CommentThreadResponse> listByDocument(UUID projectId, UUID documentId) {
        authorization.requireView(projectId);
        return threadRepo.findByDocumentId(documentId).stream()
                .map(thread -> {
                    List<CommentResponse> comments = commentRepo.findByThreadId(thread.id())
                            .stream().map(CommentResponse::from).toList();
                    return CommentThreadResponse.from(thread, comments);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentThreadResponse get(UUID projectId, UUID documentId, UUID threadId) {
        authorization.requireView(projectId);
        var thread = threadRepo.findByIdAndDocumentId(threadId, documentId)
                .orElseThrow(() -> DocumentHubExceptions.commentThreadNotFound(threadId));
        List<CommentResponse> comments = commentRepo.findByThreadId(thread.id())
                .stream().map(CommentResponse::from).toList();
        return CommentThreadResponse.from(thread, comments);
    }
}
