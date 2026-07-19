package com.company.scopery.modules.quality.testsuite.domain.model;
import java.util.*;
public interface TestSuiteRepository {
    TestSuite save(TestSuite entity);
    Optional<TestSuite> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestSuite> findByProjectId(UUID projectId);
    List<TestSuite> findByProjectIdAndTestPlanId(UUID projectId, UUID testPlanId);
}
