package com.company.scopery.modules.scope.mapping.infrastructure.persistence;
import com.company.scopery.modules.scope.mapping.domain.model.DeliverableTaskMapping;
import com.company.scopery.modules.scope.mapping.domain.model.DeliverableTaskMappingRepository;
import com.company.scopery.modules.scope.mapping.infrastructure.mapper.DeliverableTaskMappingPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaDeliverableTaskMappingRepository implements DeliverableTaskMappingRepository {
    private final SpringDataDeliverableTaskMappingJpaRepository springData;
    private final DeliverableTaskMappingPersistenceMapper mapper;
    public JpaDeliverableTaskMappingRepository(SpringDataDeliverableTaskMappingJpaRepository springData,
                                               DeliverableTaskMappingPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public DeliverableTaskMapping save(DeliverableTaskMapping mapping) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(mapping)));
    }
    @Override public Optional<DeliverableTaskMapping> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<DeliverableTaskMapping> findActiveByDeliverableId(UUID deliverableId) {
        return springData.findByDeliverableIdAndArchivedAtIsNullOrderByCreatedAtDesc(deliverableId).stream()
                .map(mapper::toDomain).toList();
    }
}
