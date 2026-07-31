package com.company.scopery.modules.quality.defect.application.service;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.domain.model.Defect;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResultRepository;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunRepository;
import com.company.scopery.modules.quality.verificationcase.domain.model.VerificationCaseRepository;
import com.company.scopery.modules.quality.verificationresult.domain.model.VerificationCaseResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DefectQueryService {
    private final DefectRepository repo;
    private final QualityAuthorizationService authorization;
    private final TestCaseResultRepository testCaseResults;
    private final VerificationCaseResultRepository verificationResults;
    private final TestRunRepository runs;
    private final TestCaseRepository testCases;
    private final VerificationCaseRepository verificationCases;
    public DefectQueryService(DefectRepository repo, QualityAuthorizationService authorization,
                              TestCaseResultRepository testCaseResults,
                              VerificationCaseResultRepository verificationResults,
                              TestRunRepository runs, TestCaseRepository testCases,
                              VerificationCaseRepository verificationCases) {
        this.repo = repo; this.authorization = authorization;
        this.testCaseResults = testCaseResults; this.verificationResults = verificationResults;
        this.runs = runs; this.testCases = testCases; this.verificationCases = verificationCases;
    }
    @Transactional(readOnly = true)
    public List<DefectResponse> list(UUID projectId) {
        authorization.requireDefectView(projectId);
        return repo.findByProjectId(projectId).stream()
                .map(d -> DefectResponse.withSource(d, resolveSource(d, projectId))).toList();
    }
    @Transactional(readOnly = true)
    public DefectResponse get(UUID projectId, UUID id) {
        authorization.requireDefectView(projectId);
        Defect d = repo.findByIdAndProjectId(id, projectId)
                .orElseThrow(() -> QualityExceptions.defectNotFound(id));
        return DefectResponse.withSource(d, resolveSource(d, projectId));
    }
    private DefectResponse.DefectSourceResponse resolveSource(Defect d, UUID projectId) {
        if (d.sourceTestCaseResultId() != null) {
            var result = testCaseResults.findByIdAndProjectId(d.sourceTestCaseResultId(), projectId).orElse(null);
            if (result == null) return null;
            var run = runs.findByIdAndProjectId(result.testRunId(), projectId).orElse(null);
            var tc = testCases.findByIdAndProjectId(result.testCaseId(), projectId).orElse(null);
            return new DefectResponse.DefectSourceResponse(
                    "FUNCTIONAL",
                    result.testRunId(), run != null ? run.name() : null,
                    result.id(), result.resultStatus().name(),
                    "FUNCTIONAL", result.testCaseId(),
                    tc != null ? tc.code() : null, tc != null ? tc.title() : null,
                    result.comment());
        } else if (d.sourceVerificationResultId() != null) {
            var result = verificationResults.findByIdAndProjectId(d.sourceVerificationResultId(), projectId).orElse(null);
            if (result == null) return null;
            var run = runs.findByIdAndProjectId(result.testRunId(), projectId).orElse(null);
            var vc = verificationCases.findByIdAndProjectId(result.verificationCaseId(), projectId).orElse(null);
            return new DefectResponse.DefectSourceResponse(
                    "NFR",
                    result.testRunId(), run != null ? run.name() : null,
                    result.id(), result.resultStatus().name(),
                    "NFR", result.verificationCaseId(),
                    vc != null ? vc.code() : null, vc != null ? vc.title() : null,
                    result.comment());
        }
        return null;
    }
}
