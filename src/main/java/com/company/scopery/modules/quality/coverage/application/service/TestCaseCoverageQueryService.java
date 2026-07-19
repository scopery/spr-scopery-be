package com.company.scopery.modules.quality.coverage.application.service;
import com.company.scopery.modules.quality.coverage.application.response.TestCaseCoverageResponse;
import com.company.scopery.modules.quality.coverage.domain.model.TestCaseCoverageRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TestCaseCoverageQueryService {
    private final TestCaseCoverageRepository repo;
    private final QualityAuthorizationService authorization;
    public TestCaseCoverageQueryService(TestCaseCoverageRepository repo, QualityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<TestCaseCoverageResponse> listByTestCase(UUID projectId, UUID testCaseId) {
        authorization.requireTestView(projectId);
        return repo.findByProjectIdAndTestCaseId(projectId, testCaseId).stream().map(TestCaseCoverageResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public TestCaseCoverageResponse get(UUID projectId, UUID id) {
        authorization.requireTestView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(TestCaseCoverageResponse::from)
                .orElseThrow(() -> QualityExceptions.testCaseNotFound(id));
    }
}
