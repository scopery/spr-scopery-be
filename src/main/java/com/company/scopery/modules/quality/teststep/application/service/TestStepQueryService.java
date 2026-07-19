package com.company.scopery.modules.quality.teststep.application.service;
import com.company.scopery.modules.quality.teststep.application.response.TestStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestStepRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TestStepQueryService {
    private final TestStepRepository repo;
    private final QualityAuthorizationService authorization;
    public TestStepQueryService(TestStepRepository repo, QualityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<TestStepResponse> list(UUID projectId) {
        authorization.requireTestView(projectId);
        return repo.findByProjectId(projectId).stream().map(TestStepResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public TestStepResponse get(UUID projectId, UUID id) {
        authorization.requireTestView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(TestStepResponse::from)
                .orElseThrow(() -> QualityExceptions.testStepNotFound(id));
    }
    @Transactional(readOnly = true)
    public List<TestStepResponse> listByParent(UUID projectId, UUID testCaseId) {
        authorization.requireTestView(projectId);
        return repo.findByProjectIdAndTestCaseId(projectId, testCaseId).stream().map(TestStepResponse::from).toList();
    }
}
