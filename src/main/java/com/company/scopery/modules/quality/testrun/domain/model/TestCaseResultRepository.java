package com.company.scopery.modules.quality.testrun.domain.model;
import java.util.*;
public interface TestCaseResultRepository {
    TestCaseResult save(TestCaseResult e);
    Optional<TestCaseResult> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCaseResult> findByTestRunId(UUID testRunId);
}
