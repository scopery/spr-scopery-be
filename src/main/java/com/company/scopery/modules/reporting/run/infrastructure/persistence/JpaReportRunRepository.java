package com.company.scopery.modules.reporting.run.infrastructure.persistence;
import com.company.scopery.modules.reporting.run.domain.model.ReportRun;
import com.company.scopery.modules.reporting.run.domain.model.ReportRunRepository;
import com.company.scopery.modules.reporting.run.infrastructure.mapper.ReportRunPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaReportRunRepository implements ReportRunRepository {
    private final SpringDataReportRunJpaRepository springData;
    private final ReportRunPersistenceMapper mapper;
    public JpaReportRunRepository(SpringDataReportRunJpaRepository springData, ReportRunPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ReportRun save(ReportRun run) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(run))); }
    @Override public Optional<ReportRun> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<ReportRun> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
