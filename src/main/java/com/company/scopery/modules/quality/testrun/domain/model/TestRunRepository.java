package com.company.scopery.modules.quality.testrun.domain.model;
import java.util.*;
public interface TestRunRepository {
    TestRun save(TestRun e);
    Optional<TestRun> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestRun> findByProjectId(UUID projectId);

    List<TestRunListRow> searchList(UUID projectId, String q, String status, int limit, long offset);
    long countSearch(UUID projectId, String q, String status);
}
