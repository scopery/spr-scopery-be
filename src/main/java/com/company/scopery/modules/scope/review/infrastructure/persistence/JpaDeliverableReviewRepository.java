package com.company.scopery.modules.scope.review.infrastructure.persistence;
import com.company.scopery.modules.scope.review.domain.enums.ReviewStatus;
import com.company.scopery.modules.scope.review.domain.model.DeliverableReview;
import com.company.scopery.modules.scope.review.domain.model.DeliverableReviewRepository;
import com.company.scopery.modules.scope.review.infrastructure.mapper.DeliverableReviewPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaDeliverableReviewRepository implements DeliverableReviewRepository {
    private final SpringDataDeliverableReviewJpaRepository springData;
    private final DeliverableReviewPersistenceMapper mapper;
    public JpaDeliverableReviewRepository(SpringDataDeliverableReviewJpaRepository springData,
                                            DeliverableReviewPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public DeliverableReview save(DeliverableReview review) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(review)));
    }
    @Override public Optional<DeliverableReview> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public Optional<DeliverableReview> findOpenByDeliverableId(UUID deliverableId) {
        return springData.findByDeliverableIdAndStatus(deliverableId, ReviewStatus.OPEN.name()).map(mapper::toDomain);
    }
    @Override public List<DeliverableReview> findByDeliverableId(UUID deliverableId) {
        return springData.findByDeliverableIdOrderByCreatedAtDesc(deliverableId).stream().map(mapper::toDomain).toList();
    }
}
