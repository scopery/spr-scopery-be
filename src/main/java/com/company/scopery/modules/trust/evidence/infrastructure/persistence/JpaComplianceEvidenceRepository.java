package com.company.scopery.modules.trust.evidence.infrastructure.persistence;
import com.company.scopery.modules.trust.evidence.domain.model.*;
import com.company.scopery.modules.trust.evidence.infrastructure.mapper.ComplianceEvidencePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaComplianceEvidenceRepository implements ComplianceEvidenceRecordRepository {
    private final SpringDataComplianceEvidenceJpaRepository spring; private final ComplianceEvidencePersistenceMapper mapper;
    public JpaComplianceEvidenceRepository(SpringDataComplianceEvidenceJpaRepository spring, ComplianceEvidencePersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public ComplianceEvidenceRecord save(ComplianceEvidenceRecord r){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(r))); }
    @Override public Optional<ComplianceEvidenceRecord> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ComplianceEvidenceRecord> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
