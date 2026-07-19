package com.company.scopery.modules.clientportal.review.infrastructure.persistence;
import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewDecision;
import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewDecisionRepository;
import com.company.scopery.modules.clientportal.review.infrastructure.mapper.ClientReviewDecisionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaClientReviewDecisionRepository implements ClientReviewDecisionRepository {
    private final SpringDataClientReviewDecisionJpaRepository springData;
    private final ClientReviewDecisionPersistenceMapper mapper;
    public JpaClientReviewDecisionRepository(SpringDataClientReviewDecisionJpaRepository springData, ClientReviewDecisionPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override
    public ClientReviewDecision save(ClientReviewDecision entity) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(entity)));
    }
    @Override
    public List<ClientReviewDecision> findByReviewRequestId(UUID reviewRequestId) {
        return springData.findByReviewRequestId(reviewRequestId).stream().map(mapper::toDomain).toList();
    }
}
