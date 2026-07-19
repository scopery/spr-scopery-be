package com.company.scopery.modules.reporting.definition.infrastructure.persistence;
import com.company.scopery.modules.reporting.definition.domain.enums.ReportDefinitionStatus;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinition;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinitionRepository;
import com.company.scopery.modules.reporting.definition.infrastructure.mapper.ReportDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaReportDefinitionRepository implements ReportDefinitionRepository {
    private final SpringDataReportDefinitionJpaRepository springData;
    private final ReportDefinitionPersistenceMapper mapper;
    public JpaReportDefinitionRepository(SpringDataReportDefinitionJpaRepository springData, ReportDefinitionPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ReportDefinition save(ReportDefinition d) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(d))); }
    @Override public Optional<ReportDefinition> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public Optional<ReportDefinition> findByCode(String code) { return springData.findByCode(code).map(mapper::toDomain); }
    @Override public List<ReportDefinition> findAllActive() {
        return springData.findByStatusOrderByCodeAsc(ReportDefinitionStatus.ACTIVE.name()).stream().map(mapper::toDomain).toList();
    }
}
