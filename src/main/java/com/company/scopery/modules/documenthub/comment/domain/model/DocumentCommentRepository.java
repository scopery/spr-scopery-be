package com.company.scopery.modules.documenthub.comment.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentCommentRepository {
    DocumentComment save(DocumentComment comment);
    Optional<DocumentComment> findById(UUID id);
    List<DocumentComment> findByThreadId(UUID threadId);
}
