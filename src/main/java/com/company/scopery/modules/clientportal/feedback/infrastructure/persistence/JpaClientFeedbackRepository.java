package com.company.scopery.modules.clientportal.feedback.infrastructure.persistence;
import com.company.scopery.modules.clientportal.feedback.domain.model.*;
import com.company.scopery.modules.clientportal.feedback.infrastructure.mapper.ClientFeedbackPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaClientFeedbackRepository implements ClientFeedbackRepository {
    private final SpringDataClientFeedbackJpaRepository springData;
    private final ClientFeedbackPersistenceMapper mapper;
    public JpaClientFeedbackRepository(SpringDataClientFeedbackJpaRepository springData, ClientFeedbackPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ClientFeedback save(ClientFeedback e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ClientFeedback> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<ClientFeedback> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
