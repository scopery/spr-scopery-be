package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunMembershipItem;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunMembershipRepository;
import com.company.scopery.modules.quality.testrun.infrastructure.mapper.TestRunMembershipPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaTestRunMembershipRepository implements TestRunMembershipRepository {
    private final SpringDataTestRunMembershipJpaRepository springData;
    private final TestRunMembershipPersistenceMapper mapper;
    public JpaTestRunMembershipRepository(SpringDataTestRunMembershipJpaRepository springData, TestRunMembershipPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public TestRunMembershipItem save(TestRunMembershipItem item) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item))); }
    @Override public List<TestRunMembershipItem> findByTestRunId(UUID testRunId) { return springData.findByTestRunIdOrderByDisplayOrderAsc(testRunId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean exists(UUID testRunId, MembershipCaseKind caseKind, UUID caseId) { return springData.existsByTestRunIdAndCaseKindAndCaseId(testRunId, caseKind.name(), caseId); }
    @Override public void delete(UUID testRunId, MembershipCaseKind caseKind, UUID caseId) { springData.deleteByTestRunIdAndCaseKindAndCaseId(testRunId, caseKind.name(), caseId); }
    @Override public void deleteAllByTestRunId(UUID testRunId) { springData.deleteByTestRunId(testRunId); }
    @Override public int countByTestRunId(UUID testRunId) { return springData.countByTestRunId(testRunId); }
}
