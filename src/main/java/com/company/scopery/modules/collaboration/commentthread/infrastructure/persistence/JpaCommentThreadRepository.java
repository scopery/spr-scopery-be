package com.company.scopery.modules.collaboration.commentthread.infrastructure.persistence;
import com.company.scopery.modules.collaboration.commentthread.domain.model.*;
import com.company.scopery.modules.collaboration.commentthread.infrastructure.mapper.CommentThreadPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCommentThreadRepository implements CommentThreadRepository {
    private final SpringDataCommentThreadJpaRepository springData; private final CommentThreadPersistenceMapper mapper;
    public JpaCommentThreadRepository(SpringDataCommentThreadJpaRepository springData, CommentThreadPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CommentThread save(CommentThread t) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(t))); }
    @Override public Optional<CommentThread> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<CommentThread> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
    @Override public List<CommentThread> findByTarget(UUID projectId, String targetType, UUID targetId) {
        return springData.findByProjectIdAndTargetTypeAndTargetIdOrderByCreatedAtDesc(projectId, targetType, targetId).stream().map(mapper::toDomain).toList();
    }
}
