package com.company.scopery.modules.quality.testcase.application.service;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService; import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.application.response.TestCaseResponse; import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TestCaseQueryService {
    private final TestCaseRepository repo; private final QualityAuthorizationService authorization;
    public TestCaseQueryService(TestCaseRepository repo, QualityAuthorizationService authorization) { this.repo=repo; this.authorization=authorization; }
    @Transactional(readOnly=true) public List<TestCaseResponse> list(UUID projectId) { authorization.requireTestView(projectId); return repo.findByProjectId(projectId).stream().map(TestCaseResponse::from).toList(); }
    @Transactional(readOnly=true) public TestCaseResponse get(UUID projectId, UUID id) {
        authorization.requireTestView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(TestCaseResponse::from).orElseThrow(() -> QualityExceptions.testCaseNotFound(id));
    }
}
