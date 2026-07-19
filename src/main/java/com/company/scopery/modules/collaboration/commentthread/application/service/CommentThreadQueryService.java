package com.company.scopery.modules.collaboration.commentthread.application.service;
import com.company.scopery.modules.collaboration.commentthread.application.response.CommentThreadResponse;
import com.company.scopery.modules.collaboration.commentthread.domain.model.CommentThreadRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service("collaborationCommentThreadQueryService")
public class CommentThreadQueryService {
    private final CommentThreadRepository threads; private final CollaborationAuthorizationService authorization;
    public CommentThreadQueryService(CommentThreadRepository threads, CollaborationAuthorizationService authorization) { this.threads=threads; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public CommentThreadResponse get(UUID projectId, UUID threadId) {
        authorization.requireCommentThreadView(projectId);
        return CommentThreadResponse.from(threads.findByIdAndProjectId(threadId, projectId).orElseThrow(() -> CollaborationExceptions.threadNotFound(threadId)));
    }
    @Transactional(readOnly=true)
    public List<CommentThreadResponse> list(UUID projectId) {
        authorization.requireCommentThreadView(projectId);
        return threads.findByProjectId(projectId).stream().filter(t -> t.archivedAt() == null).map(CommentThreadResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<CommentThreadResponse> byTarget(UUID projectId, String targetType, UUID targetId) {
        authorization.requireCommentThreadView(projectId);
        return threads.findByTarget(projectId, targetType, targetId).stream().filter(t -> t.archivedAt() == null).map(CommentThreadResponse::from).toList();
    }
}
