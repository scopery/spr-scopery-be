package com.company.scopery.modules.reporting.exportjob.infrastructure.persistence;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJob;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJobRepository;
import com.company.scopery.modules.reporting.exportjob.infrastructure.mapper.ReportExportJobPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaReportExportJobRepository implements ReportExportJobRepository {
    private final SpringDataReportExportJobJpaRepository springData;
    private final ReportExportJobPersistenceMapper mapper;
    public JpaReportExportJobRepository(SpringDataReportExportJobJpaRepository springData, ReportExportJobPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ReportExportJob save(ReportExportJob job) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(job))); }
    @Override public Optional<ReportExportJob> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<ReportExportJob> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
