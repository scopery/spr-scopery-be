package com.company.scopery.modules.quality.qualityplan.infrastructure.persistence;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlan;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlanRepository;
import com.company.scopery.modules.quality.qualityplan.infrastructure.mapper.QualityPlanPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaQualityPlanRepository implements QualityPlanRepository {
    private final SpringDataQualityPlanJpaRepository springData;
    private final QualityPlanPersistenceMapper mapper;
    public JpaQualityPlanRepository(SpringDataQualityPlanJpaRepository springData, QualityPlanPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public QualityPlan save(QualityPlan e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<QualityPlan> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<QualityPlan> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
