package com.company.scopery.modules.quality.testrun.application.service;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService; import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testrun.application.response.*; import com.company.scopery.modules.quality.testrun.domain.model.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TestRunQueryService {
    private final TestRunRepository runs; private final TestCaseResultRepository results; private final QualityAuthorizationService authorization;
    public TestRunQueryService(TestRunRepository runs, TestCaseResultRepository results, QualityAuthorizationService authorization) {
        this.runs=runs; this.results=results; this.authorization=authorization;
    }
    @Transactional(readOnly=true) public List<TestRunResponse> list(UUID projectId) { authorization.requireTestView(projectId); return runs.findByProjectId(projectId).stream().map(TestRunResponse::from).toList(); }
    @Transactional(readOnly=true) public TestRunResponse get(UUID projectId, UUID id) {
        authorization.requireTestView(projectId);
        return runs.findByIdAndProjectId(id, projectId).map(TestRunResponse::from).orElseThrow(() -> QualityExceptions.testRunNotFound(id));
    }
    @Transactional(readOnly=true) public List<TestCaseResultResponse> listResults(UUID projectId, UUID testRunId) {
        authorization.requireTestView(projectId);
        runs.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        return results.findByTestRunId(testRunId).stream().map(TestCaseResultResponse::from).toList();
    }
}
