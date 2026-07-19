package com.company.scopery.modules.clientportal.review.infrastructure.persistence;
import com.company.scopery.modules.clientportal.review.domain.model.*; import com.company.scopery.modules.clientportal.review.infrastructure.mapper.ClientReviewRequestPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaClientReviewRequestRepository implements ClientReviewRequestRepository {
    private final SpringDataClientReviewRequestJpaRepository springData; private final ClientReviewRequestPersistenceMapper mapper;
    public JpaClientReviewRequestRepository(SpringDataClientReviewRequestJpaRepository springData, ClientReviewRequestPersistenceMapper mapper){this.springData=springData;this.mapper=mapper;}
    @Override public ClientReviewRequest save(ClientReviewRequest e){return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e)));}
    @Override public Optional<ClientReviewRequest> findByIdAndProjectId(UUID id, UUID projectId){return springData.findByIdAndProjectId(id,projectId).map(mapper::toDomain);}
    @Override public List<ClientReviewRequest> findByProjectId(UUID projectId){return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();}
}
