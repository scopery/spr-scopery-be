package com.company.scopery.modules.profitability.adjustment.infrastructure.persistence;
import com.company.scopery.modules.profitability.adjustment.domain.model.*;
import com.company.scopery.modules.profitability.adjustment.infrastructure.mapper.ProfitAdjustmentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaProfitAdjustmentRepository implements ProfitAdjustmentRepository {
    private final SpringDataProfitAdjustmentJpaRepository springData; private final ProfitAdjustmentPersistenceMapper mapper;
    public JpaProfitAdjustmentRepository(SpringDataProfitAdjustmentJpaRepository springData, ProfitAdjustmentPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public ProfitAdjustment save(ProfitAdjustment a) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(a))); }
    @Override public List<ProfitAdjustment> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }

    @Override public Optional<ProfitAdjustment> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
}
