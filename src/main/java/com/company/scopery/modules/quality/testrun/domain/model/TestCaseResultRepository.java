package com.company.scopery.modules.quality.testrun.domain.model;
import java.util.*;
public interface TestCaseResultRepository {
    TestCaseResult save(TestCaseResult e);
    Optional<TestCaseResult> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCaseResult> findByTestRunId(UUID testRunId);
    List<TestCaseResult> findByTestRunIdAndProjectId(UUID testRunId, UUID projectId);

    List<TestCaseResultRow> searchList(UUID projectId, UUID testRunId, String q, String result,
            UUID assigneeId, Boolean hasDefect, int limit, long offset);
    long countSearch(UUID projectId, UUID testRunId, String q, String result,
            UUID assigneeId, Boolean hasDefect);
}
