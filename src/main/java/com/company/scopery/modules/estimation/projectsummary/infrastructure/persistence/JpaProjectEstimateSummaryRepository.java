package com.company.scopery.modules.estimation.projectsummary.infrastructure.persistence;

import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummary;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummaryRepository;
import com.company.scopery.modules.estimation.projectsummary.infrastructure.mapper.ProjectEstimateSummaryPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional; import java.util.UUID;

@Repository
public class JpaProjectEstimateSummaryRepository implements ProjectEstimateSummaryRepository {
    private final SpringDataProjectEstimateSummaryJpaRepository springData;
    private final ProjectEstimateSummaryPersistenceMapper mapper;
    public JpaProjectEstimateSummaryRepository(SpringDataProjectEstimateSummaryJpaRepository springData,
                                               ProjectEstimateSummaryPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ProjectEstimateSummary save(ProjectEstimateSummary summary) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(summary)));
    }
    @Override public Optional<ProjectEstimateSummary> findByEstimationRunId(UUID estimationRunId) {
        return springData.findByEstimationRunId(estimationRunId).map(mapper::toDomain);
    }
}
