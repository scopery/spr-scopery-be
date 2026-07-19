package com.company.scopery.modules.airecommendation.application.service;

import com.company.scopery.modules.airecommendation.application.response.RecommendationRunResponse;
import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationPolicy;
import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationRun;
import com.company.scopery.modules.airecommendation.domain.repository.AiRecommendationPolicyRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiRecommendationRunRepository;
import com.company.scopery.modules.airecommendation.shared.error.AiRecommendationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RecommendationRunQueryService {

    private final AiRecommendationRunRepository runRepository;
    private final AiRecommendationPolicyRepository policyRepository;

    public RecommendationRunQueryService(AiRecommendationRunRepository runRepository,
                                          AiRecommendationPolicyRepository policyRepository) {
        this.runRepository = runRepository;
        this.policyRepository = policyRepository;
    }

    @Transactional(readOnly = true)
    public RecommendationRunResponse getRunById(UUID runId, UUID workspaceId) {
        AiRecommendationRun run = runRepository.findById(runId)
                .filter(r -> workspaceId.equals(r.workspaceId()))
                .orElseThrow(() -> AiRecommendationExceptions.runNotFound(runId));

        String policyCode = policyRepository.findById(run.policyId())
                .map(AiRecommendationPolicy::code)
                .orElse(null);

        return new RecommendationRunResponse(
                run.id(), run.workspaceId(), run.projectId(),
                run.status().name(),
                run.triggerType() != null ? run.triggerType().name() : null,
                policyCode,
                run.requestedPackCodes(),
                new RecommendationRunResponse.RunCounts(
                        run.detectorCount(), run.candidateCount(), run.persistedCount(),
                        run.deduplicatedCount(), run.suppressedCount(), run.discardedCount(),
                        run.failedDetectorCount()),
                run.startedAt(), run.completedAt(), run.latencyMs(),
                run.errorCode(), run.traceId());
    }
}
