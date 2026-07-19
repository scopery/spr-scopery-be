package com.company.scopery.modules.quality.testcase.domain.model;
import java.util.*;
public interface TestCaseRepository {
    TestCase save(TestCase e);
    Optional<TestCase> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCase> findByProjectId(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
