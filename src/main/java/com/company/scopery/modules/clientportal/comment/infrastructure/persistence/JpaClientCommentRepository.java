package com.company.scopery.modules.clientportal.comment.infrastructure.persistence;
import com.company.scopery.modules.clientportal.comment.domain.model.*;
import com.company.scopery.modules.clientportal.comment.infrastructure.mapper.ClientCommentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaClientCommentRepository implements ClientCommentRepository {
    private final SpringDataClientCommentJpaRepository springData;
    private final ClientCommentPersistenceMapper mapper;
    public JpaClientCommentRepository(SpringDataClientCommentJpaRepository springData, ClientCommentPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ClientComment save(ClientComment e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public List<ClientComment> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override public List<ClientComment> findByProjectIdAndTargetTypeAndTargetId(UUID projectId, String targetType, UUID targetId) {
        return springData.findByProjectIdAndTargetTypeAndTargetIdOrderByCreatedAtDesc(projectId, targetType, targetId).stream().map(mapper::toDomain).toList();
    }
}
