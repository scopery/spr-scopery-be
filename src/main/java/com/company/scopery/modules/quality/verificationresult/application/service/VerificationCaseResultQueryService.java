package com.company.scopery.modules.quality.verificationresult.application.service;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunRepository;
import com.company.scopery.modules.quality.verificationresult.application.response.VerificationCaseResultResponse;
import com.company.scopery.modules.quality.verificationresult.domain.model.VerificationCaseResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class VerificationCaseResultQueryService {
    private final VerificationCaseResultRepository repo;
    private final TestRunRepository testRunRepo;
    private final QualityAuthorizationService authorization;
    public VerificationCaseResultQueryService(VerificationCaseResultRepository repo, TestRunRepository testRunRepo, QualityAuthorizationService authorization) {
        this.repo=repo; this.testRunRepo=testRunRepo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<VerificationCaseResultResponse> listByTestRun(UUID projectId, UUID testRunId) {
        authorization.requireTestView(projectId);
        testRunRepo.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        return repo.findByTestRunIdAndProjectId(testRunId, projectId).stream().map(VerificationCaseResultResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public VerificationCaseResultResponse getById(UUID projectId, UUID resultId) {
        authorization.requireTestView(projectId);
        return repo.findByIdAndProjectId(resultId, projectId).map(VerificationCaseResultResponse::from).orElseThrow(() -> QualityExceptions.verificationResultNotFound(resultId));
    }
}
