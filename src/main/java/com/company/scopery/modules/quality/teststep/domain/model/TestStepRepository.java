package com.company.scopery.modules.quality.teststep.domain.model;
import java.util.*;
public interface TestStepRepository {
    TestStep save(TestStep entity);
    Optional<TestStep> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestStep> findByProjectId(UUID projectId);
    java.util.List<TestStep> findByProjectIdAndTestCaseId(UUID projectId, UUID parentId);

}
