package com.company.scopery.modules.quality.verificationresult.infrastructure.persistence;
import com.company.scopery.modules.quality.verificationresult.domain.model.*;
import com.company.scopery.modules.quality.verificationresult.infrastructure.mapper.VerificationCaseResultPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaVerificationCaseResultRepository implements VerificationCaseResultRepository {
    private final SpringDataVerificationCaseResultJpaRepository springData;
    private final VerificationCaseResultPersistenceMapper mapper;
    public JpaVerificationCaseResultRepository(SpringDataVerificationCaseResultJpaRepository springData, VerificationCaseResultPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public VerificationCaseResult save(VerificationCaseResult r) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(r))); }
    @Override public Optional<VerificationCaseResult> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public Optional<VerificationCaseResult> findByTestRunIdAndVerificationCaseId(UUID testRunId, UUID verificationCaseId) { return springData.findByTestRunIdAndVerificationCaseId(testRunId, verificationCaseId).map(mapper::toDomain); }
    @Override public List<VerificationCaseResult> findByTestRunIdAndProjectId(UUID testRunId, UUID projectId) { return springData.findByTestRunIdAndProjectId(testRunId, projectId).stream().map(mapper::toDomain).toList(); }
}
