package com.company.scopery.modules.documenthub.comment.domain.model;

import com.company.scopery.modules.documenthub.comment.domain.enums.CommentThreadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentCommentThreadRepository {
    DocumentCommentThread save(DocumentCommentThread thread);
    Optional<DocumentCommentThread> findByIdAndDocumentId(UUID id, UUID documentId);
    List<DocumentCommentThread> findByDocumentId(UUID documentId);
    List<DocumentCommentThread> findByDocumentIdAndStatus(UUID documentId, CommentThreadStatus status);
}
