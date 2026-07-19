package com.company.scopery.modules.collaboration.comment.infrastructure.persistence;
import com.company.scopery.modules.collaboration.comment.domain.model.*;
import com.company.scopery.modules.collaboration.comment.infrastructure.mapper.CommentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCommentRepository implements CommentRepository {
    private final SpringDataCommentJpaRepository springData; private final CommentPersistenceMapper mapper;
    public JpaCommentRepository(SpringDataCommentJpaRepository springData, CommentPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public Comment save(Comment c) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(c))); }
    @Override public Optional<Comment> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<Comment> findByThreadId(UUID threadId) { return springData.findByThreadIdOrderByCreatedAtAsc(threadId).stream().map(mapper::toDomain).toList(); }
}
