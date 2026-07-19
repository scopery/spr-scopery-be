package com.company.scopery.modules.scope.evidence.infrastructure.persistence;
import com.company.scopery.modules.scope.evidence.domain.model.AcceptanceEvidence;
import com.company.scopery.modules.scope.evidence.domain.model.AcceptanceEvidenceRepository;
import com.company.scopery.modules.scope.evidence.infrastructure.mapper.AcceptanceEvidencePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaAcceptanceEvidenceRepository implements AcceptanceEvidenceRepository {
    private final SpringDataAcceptanceEvidenceJpaRepository springData;
    private final AcceptanceEvidencePersistenceMapper mapper;
    public JpaAcceptanceEvidenceRepository(SpringDataAcceptanceEvidenceJpaRepository springData,
                                           AcceptanceEvidencePersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public AcceptanceEvidence save(AcceptanceEvidence evidence) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(evidence)));
    }
    @Override public Optional<AcceptanceEvidence> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<AcceptanceEvidence> findByDeliverableId(UUID deliverableId) {
        return springData.findByDeliverableIdOrderByCreatedAtDesc(deliverableId).stream().map(mapper::toDomain).toList();
    }
    @Override public long countByProjectId(UUID projectId) { return springData.countByProjectId(projectId); }
}
