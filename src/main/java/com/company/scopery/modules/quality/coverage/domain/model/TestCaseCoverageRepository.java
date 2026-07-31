package com.company.scopery.modules.quality.coverage.domain.model;
import java.util.*;
public interface TestCaseCoverageRepository {
    TestCaseCoverage save(TestCaseCoverage entity);
    Optional<TestCaseCoverage> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCaseCoverage> findByProjectIdAndTestCaseId(UUID projectId, UUID testCaseId);
    List<TestCaseCoverage> findByProjectIdAndTestCaseIdAndTargetType(UUID projectId, UUID testCaseId, String targetType);
    void saveAll(List<TestCaseCoverage> entities);
}
