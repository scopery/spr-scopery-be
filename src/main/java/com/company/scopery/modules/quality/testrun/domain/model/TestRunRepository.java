package com.company.scopery.modules.quality.testrun.domain.model;
import java.util.*;
public interface TestRunRepository {
    TestRun save(TestRun e);
    Optional<TestRun> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestRun> findByProjectId(UUID projectId);
}
