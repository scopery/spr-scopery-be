package com.company.scopery.modules.quality.testsuite.application.service;
import com.company.scopery.modules.quality.testsuite.application.response.TestSuiteResponse;
import com.company.scopery.modules.quality.testsuite.domain.model.TestSuiteRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TestSuiteQueryService {
    private final TestSuiteRepository repo;
    private final QualityAuthorizationService authorization;
    public TestSuiteQueryService(TestSuiteRepository repo, QualityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public List<TestSuiteResponse> list(UUID projectId) {
        authorization.requireTestView(projectId);
        return repo.findByProjectId(projectId).stream().map(TestSuiteResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public List<TestSuiteResponse> listByTestPlan(UUID projectId, UUID testPlanId) {
        authorization.requireTestView(projectId);
        return repo.findByProjectIdAndTestPlanId(projectId, testPlanId).stream().map(TestSuiteResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public TestSuiteResponse get(UUID projectId, UUID id) {
        authorization.requireTestView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(TestSuiteResponse::from)
                .orElseThrow(() -> QualityExceptions.testSuiteNotFound(id));
    }
}
