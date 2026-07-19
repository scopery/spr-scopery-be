package com.company.scopery.modules.documenthub.comment.infrastructure.persistence;

import com.company.scopery.modules.documenthub.comment.domain.model.DocumentComment;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentRepository;
import com.company.scopery.modules.documenthub.comment.infrastructure.mapper.DocumentCommentPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentCommentRepository implements DocumentCommentRepository {

    private final SpringDataDocumentCommentJpaRepository springData;
    private final DocumentCommentPersistenceMapper mapper;

    public JpaDocumentCommentRepository(SpringDataDocumentCommentJpaRepository springData,
                                         DocumentCommentPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public DocumentComment save(DocumentComment comment) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(comment)));
    }

    @Override
    public Optional<DocumentComment> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<DocumentComment> findByThreadId(UUID threadId) {
        return springData.findByThreadIdOrderByCreatedAtAsc(threadId).stream().map(mapper::toDomain).toList();
    }
}
