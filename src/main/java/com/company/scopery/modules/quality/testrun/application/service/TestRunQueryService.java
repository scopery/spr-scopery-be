package com.company.scopery.modules.quality.testrun.application.service;
import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.quality.testrun.application.response.*;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import com.company.scopery.modules.quality.testrun.domain.model.*;
import com.company.scopery.modules.quality.verificationcase.domain.model.VerificationCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class TestRunQueryService {
    private final TestRunRepository runs;
    private final TestCaseResultRepository results;
    private final TestRunMembershipRepository membership;
    private final TestCaseRepository testCases;
    private final VerificationCaseRepository verificationCases;
    private final QualityAuthorizationService authorization;

    public TestRunQueryService(TestRunRepository runs, TestCaseResultRepository results,
            TestRunMembershipRepository membership, TestCaseRepository testCases,
            VerificationCaseRepository verificationCases, QualityAuthorizationService authorization) {
        this.runs = runs; this.results = results; this.membership = membership;
        this.testCases = testCases; this.verificationCases = verificationCases; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public PageResponse<TestRunListItemResponse> listWithProgress(UUID projectId, String q, String status, int page, int size) {
        authorization.requireTestView(projectId);
        List<TestRunListRow> rows = runs.searchList(projectId, q, status, size, (long) page * size);
        long totalElements = runs.countSearch(projectId, q, status);
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / size);
        var items = rows.stream().map(this::toListItem).toList();
        return new PageResponse<>(items, page, size, totalElements, totalPages, page == 0, (long)(page+1)*size >= totalElements);
    }

    @Transactional(readOnly = true)
    public List<TestRunResponse> list(UUID projectId) {
        authorization.requireTestView(projectId);
        return runs.findByProjectId(projectId).stream().map(TestRunResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TestRunResponse get(UUID projectId, UUID id) {
        authorization.requireTestView(projectId);
        return runs.findByIdAndProjectId(id, projectId).map(TestRunResponse::from).orElseThrow(() -> QualityExceptions.testRunNotFound(id));
    }

    @Transactional(readOnly = true)
    public RunMembershipResponse getMembership(UUID projectId, UUID testRunId) {
        authorization.requireTestView(projectId);
        runs.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        var items = membership.findByTestRunId(testRunId).stream()
                .map(item -> enrichItem(item, projectId)).toList();
        return new RunMembershipResponse(testRunId, items);
    }

    @Transactional(readOnly = true)
    public PageResponse<TestCaseResultResponse> listResultsPaged(UUID projectId, UUID testRunId, String q,
            String result, UUID assigneeId, Boolean hasDefect, int page, int size) {
        authorization.requireTestView(projectId);
        runs.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        List<TestCaseResultRow> rows = results.searchList(projectId, testRunId, q, result, assigneeId, hasDefect, size, (long) page * size);
        long totalElements = results.countSearch(projectId, testRunId, q, result, assigneeId, hasDefect);
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / size);
        var items = rows.stream().map(this::toResultItem).toList();
        return new PageResponse<>(items, page, size, totalElements, totalPages, page == 0, (long)(page+1)*size >= totalElements);
    }

    @Transactional(readOnly = true)
    public List<TestCaseResultResponse> listResults(UUID projectId, UUID testRunId) {
        authorization.requireTestView(projectId);
        runs.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        return results.findByTestRunIdAndProjectId(testRunId, projectId).stream().map(TestCaseResultResponse::from).toList();
    }

    private TestRunListItemResponse toListItem(TestRunListRow row) {
        return new TestRunListItemResponse(row.id(), row.projectId(), row.name(), row.runType(), row.runScope(), row.status(),
                row.total(), row.executed(), row.passed(), row.failed(), row.blocked(), row.skipped(),
                row.startedAt(), row.completedAt(), row.createdAt(),
                row.releasePackageName(), row.deploymentEnvironmentName());
    }

    private TestCaseResultResponse toResultItem(TestCaseResultRow row) {
        return new TestCaseResultResponse(row.id(), row.testRunId(),
                new TestCaseResultResponse.TestCaseSummary(row.tcId(), row.tcCode(), row.tcTitle()),
                row.resultStatus(), row.defectId(), row.comment(), row.assigneeId(),
                row.executedAt(), row.createdAt(), row.version());
    }

    private RunMembershipItemResponse enrichItem(TestRunMembershipItem item, UUID projectId) {
        String code = null, title = null;
        if (item.caseKind() == MembershipCaseKind.FUNCTIONAL) {
            var tc = testCases.findByIdAndProjectId(item.caseId(), projectId).orElse(null);
            if (tc != null) { code = tc.code(); title = tc.title(); }
        } else {
            var vc = verificationCases.findByIdAndProjectId(item.caseId(), projectId).orElse(null);
            if (vc != null) { code = vc.code(); title = vc.title(); }
        }
        return new RunMembershipItemResponse(item.caseKind().name(), item.caseId(), code, title, null, item.displayOrder());
    }
}
