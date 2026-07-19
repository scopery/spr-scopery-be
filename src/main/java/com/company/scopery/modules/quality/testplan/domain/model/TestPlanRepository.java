package com.company.scopery.modules.quality.testplan.domain.model;
import java.util.*;
public interface TestPlanRepository {
    TestPlan save(TestPlan entity);
    Optional<TestPlan> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestPlan> findByProjectId(UUID projectId);

}
