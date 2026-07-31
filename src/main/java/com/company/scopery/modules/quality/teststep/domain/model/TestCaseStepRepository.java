package com.company.scopery.modules.quality.teststep.domain.model;
import java.util.*;
public interface TestCaseStepRepository {
    TestCaseStep save(TestCaseStep entity);
    Optional<TestCaseStep> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCaseStep> findByTestCaseIdOrderBySortOrder(UUID testCaseId);
    int findMaxSortOrderByTestCaseId(UUID testCaseId);
    void saveAll(List<TestCaseStep> entities);
    long countActiveByTestCaseId(UUID testCaseId);
}
