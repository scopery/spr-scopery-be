package com.company.scopery.modules.trust.anonymization.infrastructure.persistence;
import com.company.scopery.modules.trust.anonymization.domain.model.AnonymizationPlan;
import com.company.scopery.modules.trust.anonymization.domain.model.AnonymizationPlanRepository;
import com.company.scopery.modules.trust.anonymization.infrastructure.mapper.AnonymizationPlanPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaAnonymizationPlanRepository implements AnonymizationPlanRepository {
    private final SpringDataAnonymizationPlanJpaRepository spring;
    private final AnonymizationPlanPersistenceMapper mapper;
    public JpaAnonymizationPlanRepository(SpringDataAnonymizationPlanJpaRepository spring, AnonymizationPlanPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public AnonymizationPlan save(AnonymizationPlan plan) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(plan)));
    }
    @Override public Optional<AnonymizationPlan> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }
    @Override public List<AnonymizationPlan> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
