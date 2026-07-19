package com.company.scopery.modules.collaboration.comment.application.service;
import com.company.scopery.modules.collaboration.comment.application.response.CommentResponse;
import com.company.scopery.modules.collaboration.comment.domain.enums.CommentStatus;
import com.company.scopery.modules.collaboration.comment.domain.model.CommentRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class CommentQueryService {
    private final CommentRepository comments; private final CollaborationAuthorizationService authorization;
    public CommentQueryService(CommentRepository comments, CollaborationAuthorizationService authorization) { this.comments=comments; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public List<CommentResponse> listByThread(UUID projectId, UUID threadId) {
        authorization.requireCommentThreadView(projectId);
        return comments.findByThreadId(threadId).stream().filter(c -> c.status() != CommentStatus.DELETED_SOFT).map(CommentResponse::from).toList();
    }
}
