package com.company.scopery.modules.scope.review.infrastructure.persistence;
import com.company.scopery.modules.scope.review.domain.model.DeliverableAcceptance;
import com.company.scopery.modules.scope.review.domain.model.DeliverableAcceptanceRepository;
import com.company.scopery.modules.scope.review.infrastructure.mapper.DeliverableAcceptancePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional; import java.util.UUID;
@Repository
public class JpaDeliverableAcceptanceRepository implements DeliverableAcceptanceRepository {
    private final SpringDataDeliverableAcceptanceJpaRepository springData;
    private final DeliverableAcceptancePersistenceMapper mapper;
    public JpaDeliverableAcceptanceRepository(SpringDataDeliverableAcceptanceJpaRepository springData,
                                                DeliverableAcceptancePersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public DeliverableAcceptance save(DeliverableAcceptance acceptance) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(acceptance)));
    }
    @Override public Optional<DeliverableAcceptance> findLatestByDeliverableId(UUID deliverableId) {
        return springData.findFirstByDeliverableIdOrderByAcceptedAtDesc(deliverableId).map(mapper::toDomain);
    }
}
