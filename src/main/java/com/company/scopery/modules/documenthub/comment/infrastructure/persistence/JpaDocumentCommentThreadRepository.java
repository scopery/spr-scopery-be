package com.company.scopery.modules.documenthub.comment.infrastructure.persistence;

import com.company.scopery.modules.documenthub.comment.domain.enums.CommentThreadStatus;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThread;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThreadRepository;
import com.company.scopery.modules.documenthub.comment.infrastructure.mapper.DocumentCommentThreadPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentCommentThreadRepository implements DocumentCommentThreadRepository {

    private final SpringDataDocumentCommentThreadJpaRepository springData;
    private final DocumentCommentThreadPersistenceMapper mapper;

    public JpaDocumentCommentThreadRepository(SpringDataDocumentCommentThreadJpaRepository springData,
                                               DocumentCommentThreadPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public DocumentCommentThread save(DocumentCommentThread thread) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(thread)));
    }

    @Override
    public Optional<DocumentCommentThread> findByIdAndDocumentId(UUID id, UUID documentId) {
        return springData.findByIdAndDocumentId(id, documentId).map(mapper::toDomain);
    }

    @Override
    public List<DocumentCommentThread> findByDocumentId(UUID documentId) {
        return springData.findByDocumentIdOrderByCreatedAtAsc(documentId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<DocumentCommentThread> findByDocumentIdAndStatus(UUID documentId, CommentThreadStatus status) {
        return springData.findByDocumentIdAndStatusOrderByCreatedAtAsc(documentId, status.name())
                .stream().map(mapper::toDomain).toList();
    }
}
