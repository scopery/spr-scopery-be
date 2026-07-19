package com.company.scopery.modules.reporting.snapshot.infrastructure.persistence;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshot;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshotRepository;
import com.company.scopery.modules.reporting.snapshot.infrastructure.mapper.ReportSnapshotPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional; import java.util.UUID;
@Repository
public class JpaReportSnapshotRepository implements ReportSnapshotRepository {
    private final SpringDataReportSnapshotJpaRepository springData;
    private final ReportSnapshotPersistenceMapper mapper;
    public JpaReportSnapshotRepository(SpringDataReportSnapshotJpaRepository springData, ReportSnapshotPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ReportSnapshot save(ReportSnapshot s) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(s))); }
    @Override public Optional<ReportSnapshot> findByReportRunId(UUID reportRunId) {
        return springData.findByReportRunId(reportRunId).map(mapper::toDomain);
    }

    @Override
    public int deleteOlderThan(java.time.Instant cutoff) {
        return springData.deleteByGeneratedAtBefore(cutoff);
    }
}
