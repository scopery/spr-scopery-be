package com.company.scopery.modules.quality.testcase.application.service;
import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.quality.coverage.domain.model.TestCaseCoverageRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.application.response.*;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseListRow;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class TestCaseQueryService {
    private final TestCaseRepository repo;
    private final TestCaseCoverageRepository coverageRepo;
    private final TestCaseStepRepository stepRepo;
    private final QualityAuthorizationService authorization;

    public TestCaseQueryService(TestCaseRepository repo, TestCaseCoverageRepository coverageRepo,
                                TestCaseStepRepository stepRepo, QualityAuthorizationService authorization) {
        this.repo = repo; this.coverageRepo = coverageRepo; this.stepRepo = stepRepo; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public PageResponse<TestCaseListItemResponse> list(UUID projectId, String q, String type, String priority,
            String status, UUID assigneeId, String automationStatus, UUID requirementId, UUID useCaseId,
            String latestResult, Boolean hasOpenDefect, String sort, int page, int size) {
        authorization.requireTestView(projectId);
        String orderBy = resolveSort(sort);
        List<TestCaseListRow> rows = repo.searchList(projectId, q, type, priority, status, assigneeId,
                automationStatus, requirementId, useCaseId, latestResult, hasOpenDefect,
                orderBy, size, (long) page * size);
        long totalElements = repo.countSearch(projectId, q, type, priority, status, assigneeId,
                automationStatus, requirementId, useCaseId, latestResult, hasOpenDefect);
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / size);
        var items = rows.stream().map(this::toListItem).toList();
        return new PageResponse<>(items, page, size, totalElements, totalPages, page == 0, (long)(page+1)*size >= totalElements);
    }

    @Transactional(readOnly = true)
    public TestCaseDetailResponse getDetail(UUID projectId, UUID testCaseId) {
        authorization.requireTestView(projectId);
        var tc = repo.findByIdAndProjectId(testCaseId, projectId).orElseThrow(() -> QualityExceptions.testCaseNotFound(testCaseId));
        long stepCount = stepRepo.countActiveByTestCaseId(testCaseId);
        var reqCount = coverageRepo.findByProjectIdAndTestCaseIdAndTargetType(projectId, testCaseId, "REQUIREMENT").stream().filter(c -> c.archivedAt() == null).count();
        var ucCount = coverageRepo.findByProjectIdAndTestCaseIdAndTargetType(projectId, testCaseId, "USE_CASE").stream().filter(c -> c.archivedAt() == null).count();
        return new TestCaseDetailResponse(tc.id(), tc.projectId(), tc.code(), tc.title(), tc.description(),
                tc.type().name(), tc.priority().name(), tc.status().name(), tc.assigneeId(),
                tc.automationStatus() != null ? tc.automationStatus().name() : "MANUAL",
                tc.preconditions(), tc.expectedResult(), stepCount, reqCount, ucCount,
                null, null, 0L, List.of(), tc.createdAt(), tc.updatedAt(), (long) tc.version(), tc.useCaseId());
    }

    @Transactional(readOnly = true)
    public TestCaseResponse get(UUID projectId, UUID id) {
        authorization.requireTestView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(TestCaseResponse::from).orElseThrow(() -> QualityExceptions.testCaseNotFound(id));
    }

    @Transactional(readOnly = true)
    public TestCaseTraceabilityResponse getTraceability(UUID projectId, UUID testCaseId) {
        authorization.requireTestView(projectId);
        repo.findByIdAndProjectId(testCaseId, projectId).orElseThrow(() -> QualityExceptions.testCaseNotFound(testCaseId));
        var reqs = coverageRepo.findByProjectIdAndTestCaseIdAndTargetType(projectId, testCaseId, "REQUIREMENT")
                .stream().filter(c -> c.archivedAt() == null)
                .map(c -> new TestCaseTraceabilityResponse.TraceLink(c.targetId(), "REQUIREMENT", null, null, false))
                .toList();
        var ucs = coverageRepo.findByProjectIdAndTestCaseIdAndTargetType(projectId, testCaseId, "USE_CASE")
                .stream().filter(c -> c.archivedAt() == null)
                .map(c -> new TestCaseTraceabilityResponse.TraceLink(c.targetId(), "USE_CASE", null, null, false))
                .toList();
        return new TestCaseTraceabilityResponse(reqs, ucs, List.of(), List.of(), List.of(), List.of());
    }

    private String resolveSort(String sort) {
        if (sort == null || sort.isBlank()) return "tc.updated_at DESC";
        String[] parts = sort.split(",");
        String field = switch (parts[0].trim()) {
            case "code" -> "tc.code";
            case "title" -> "tc.title";
            case "priority" -> "tc.priority";
            case "status" -> "tc.status";
            case "createdAt" -> "tc.created_at";
            default -> "tc.updated_at";
        };
        String dir = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        return field + " " + dir;
    }

    private TestCaseListItemResponse toListItem(TestCaseListRow row) {
        TestCaseListItemResponse.AssigneeRef assignee = row.assigneeId() != null
                ? new TestCaseListItemResponse.AssigneeRef(row.assigneeId(), row.assigneeDisplayName()) : null;
        return new TestCaseListItemResponse(row.id(), row.projectId(), row.code(), row.title(),
                row.type(), row.priority(), row.status(), assignee, row.automationStatus(),
                row.stepCount(), row.reqCount(), row.ucCount(), row.latestResult(), row.latestResultAt(), 0L,
                row.createdAt(), row.updatedAt(), row.version(), row.useCaseId());
    }
}
