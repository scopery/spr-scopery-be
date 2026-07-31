package com.company.scopery.modules.quality.testcase.domain.model;
import java.util.*;
public interface TestCaseRepository {
    TestCase save(TestCase e);
    Optional<TestCase> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCase> findByProjectId(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);

    List<TestCaseListRow> searchList(UUID projectId, String q, String type, String priority,
            String status, UUID assigneeId, String automationStatus, UUID requirementId, UUID useCaseId,
            String latestResult, Boolean hasOpenDefect, String orderBy, int limit, long offset);
    long countSearch(UUID projectId, String q, String type, String priority,
            String status, UUID assigneeId, String automationStatus, UUID requirementId, UUID useCaseId,
            String latestResult, Boolean hasOpenDefect);
}
