package com.company.scopery.modules.servicesupport.effort.infrastructure.persistence;

import com.company.scopery.modules.servicesupport.effort.domain.model.SupportEffortRecord;
import com.company.scopery.modules.servicesupport.effort.domain.model.SupportEffortRecordRepository;
import com.company.scopery.modules.servicesupport.effort.infrastructure.mapper.SupportEffortPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;

@Repository
public class JpaSupportEffortRecordRepository implements SupportEffortRecordRepository {
    private final SpringDataSupportEffortRecordJpaRepository spring;
    private final SupportEffortPersistenceMapper mapper;
    public JpaSupportEffortRecordRepository(SpringDataSupportEffortRecordJpaRepository spring, SupportEffortPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public SupportEffortRecord save(SupportEffortRecord r) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(r))); }
    @Override public java.util.Optional<SupportEffortRecord> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportEffortRecord> findBySupportCaseId(UUID caseId) {
        return spring.findBySupportCaseId(caseId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<SupportEffortRecord> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
