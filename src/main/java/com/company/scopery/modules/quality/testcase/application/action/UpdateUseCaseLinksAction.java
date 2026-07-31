package com.company.scopery.modules.quality.testcase.application.action;
import com.company.scopery.modules.quality.coverage.domain.enums.CoverageType;
import com.company.scopery.modules.quality.coverage.domain.model.TestCaseCoverage;
import com.company.scopery.modules.quality.coverage.domain.model.TestCaseCoverageRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.application.response.TestCaseTraceabilityResponse;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Component
public class UpdateUseCaseLinksAction {
    private final TestCaseRepository testCases;
    private final TestCaseCoverageRepository coverage;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public UpdateUseCaseLinksAction(TestCaseRepository testCases, TestCaseCoverageRepository coverage,
                                    QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.testCases=testCases; this.coverage=coverage; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseTraceabilityResponse execute(UUID projectId, UUID testCaseId, List<UUID> useCaseIds) {
        authorization.requireTestUpdate(projectId);
        testCases.findByIdAndProjectId(testCaseId, projectId).orElseThrow(() -> QualityExceptions.testCaseNotFound(testCaseId));
        var existing = coverage.findByProjectIdAndTestCaseIdAndTargetType(projectId, testCaseId, "USE_CASE");
        var newIds = useCaseIds != null ? new HashSet<>(useCaseIds) : new HashSet<UUID>();
        List<TestCaseCoverage> toSave = new ArrayList<>();
        for (var cov : existing) {
            if (!newIds.contains(cov.targetId())) {
                toSave.add(cov.archive(null));
            } else {
                newIds.remove(cov.targetId());
            }
        }
        coverage.saveAll(toSave);
        for (UUID ucId : newIds) {
            coverage.save(TestCaseCoverage.create(projectId, testCaseId, "USE_CASE", ucId, CoverageType.PRIMARY));
        }
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE, testCaseId, QualityActivityActions.TEST_CASE_USE_CASE_LINKS_UPDATED, "Use case links updated");
        var reqActive = coverage.findByProjectIdAndTestCaseIdAndTargetType(projectId, testCaseId, "REQUIREMENT")
                .stream().filter(c -> c.archivedAt() == null)
                .map(c -> new TestCaseTraceabilityResponse.TraceLink(c.targetId(), "REQUIREMENT", null, null, false))
                .toList();
        var ucActive = coverage.findByProjectIdAndTestCaseIdAndTargetType(projectId, testCaseId, "USE_CASE")
                .stream().filter(c -> c.archivedAt() == null)
                .map(c -> new TestCaseTraceabilityResponse.TraceLink(c.targetId(), "USE_CASE", null, null, false))
                .toList();
        return new TestCaseTraceabilityResponse(reqActive, ucActive, List.of(), List.of(), List.of(), List.of());
    }
}
