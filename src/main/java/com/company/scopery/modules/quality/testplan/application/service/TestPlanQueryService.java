package com.company.scopery.modules.quality.testplan.application.service;
import com.company.scopery.modules.quality.testplan.application.response.TestPlanResponse;
import com.company.scopery.modules.quality.testplan.domain.model.TestPlanRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TestPlanQueryService {
    private final TestPlanRepository repo;
    private final QualityAuthorizationService authorization;
    public TestPlanQueryService(TestPlanRepository repo, QualityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public List<TestPlanResponse> list(UUID projectId) {
        authorization.requireTestView(projectId);
        return repo.findByProjectId(projectId).stream().map(TestPlanResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public TestPlanResponse get(UUID projectId, UUID id) {
        authorization.requireTestView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(TestPlanResponse::from)
                .orElseThrow(() -> QualityExceptions.testPlanNotFound(id));
    }
}
