package com.company.scopery.modules.airecommendation.application.action;

import com.company.scopery.modules.airecommendation.application.command.CreateRecommendationRunCommand;
import com.company.scopery.modules.airecommendation.application.orchestrator.RecommendationRunOrchestrator;
import com.company.scopery.modules.airecommendation.application.response.CreateRunResponse;
import com.company.scopery.modules.airecommendation.domain.enums.PolicyStatus;
import com.company.scopery.modules.airecommendation.domain.enums.RunStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationPolicy;
import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationRun;
import com.company.scopery.modules.airecommendation.domain.repository.AiRecommendationPolicyRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiRecommendationRunRepository;
import com.company.scopery.modules.airecommendation.shared.activity.AiRecommendationActivityLogger;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationActivityActions;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationEntityTypes;
import com.company.scopery.modules.airecommendation.shared.error.AiRecommendationExceptions;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateRecommendationRunAction {

    private static final String SOURCE_SYSTEM = "SCOPERY_AI_RECOMMENDATION";
    private static final String EVENT_RUN_REQUESTED = "AI_RECOMMENDATION_RUN_REQUESTED";

    private final AiRecommendationPolicyRepository policyRepository;
    private final AiRecommendationRunRepository runRepository;
    private final RecommendationRunOrchestrator orchestrator;
    private final TransactionalOutboxService outbox;
    private final AiRecommendationActivityLogger activityLogger;

    public CreateRecommendationRunAction(AiRecommendationPolicyRepository policyRepository,
                                         AiRecommendationRunRepository runRepository,
                                         RecommendationRunOrchestrator orchestrator,
                                         TransactionalOutboxService outbox,
                                         AiRecommendationActivityLogger activityLogger) {
        this.policyRepository = policyRepository;
        this.runRepository = runRepository;
        this.orchestrator = orchestrator;
        this.outbox = outbox;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CreateRunResponse execute(CreateRecommendationRunCommand cmd) {
        AiRecommendationPolicy policy = policyRepository.findByCode(cmd.policyCode())
                .orElseThrow(() -> AiRecommendationExceptions.policyNotFound(cmd.policyCode()));

        if (policy.status() != PolicyStatus.ACTIVE) {
            throw AiRecommendationExceptions.policyInactive(cmd.policyCode());
        }

        // Idempotency check
        if (cmd.idempotencyKey() != null) {
            var existing = runRepository.findByWorkspaceProjectAndIdempotencyKey(
                    cmd.workspaceId(), cmd.projectId(), cmd.idempotencyKey());
            if (existing.isPresent()) {
                AiRecommendationRun run = existing.get();
                return toResponse(run, policy);
            }
        }

        List<String> effectivePackCodes = (cmd.packCodes() != null && !cmd.packCodes().isEmpty())
                ? cmd.packCodes()
                : policy.packCodes();

        OffsetDateTime now = OffsetDateTime.now();
        AiRecommendationRun run = new AiRecommendationRun(
                UUID.randomUUID(), policy.id(), cmd.workspaceId(), cmd.projectId(),
                cmd.actorId(), cmd.triggerType(), cmd.idempotencyKey(), null,
                RunStatus.PENDING, effectivePackCodes, List.of(),
                cmd.originConversationId(), cmd.originMessageId(), cmd.originTurnId(),
                0, 0, 0, 0, 0, 0, 0, null, null, null, cmd.traceId(),
                now, null, now, now, 0L);

        run = runRepository.save(run);

        outbox.enqueue(AiRecommendationEntityTypes.RUN, run.id(),
                EVENT_RUN_REQUESTED, SOURCE_SYSTEM, 1,
                Map.of("runId", run.id(), "projectId", cmd.projectId(), "policyCode", cmd.policyCode()));

        activityLogger.logSuccess(AiRecommendationEntityTypes.RUN, run.id(),
                AiRecommendationActivityActions.CREATE_RUN, "Recommendation run created for policy: " + cmd.policyCode());

        // Synchronous orchestration for MVP MANUAL trigger
        orchestrator.orchestrate(run, policy, effectivePackCodes);

        // Reload run to get updated status/counts from orchestration
        run = runRepository.findById(run.id()).orElse(run);

        return toResponse(run, policy);
    }

    private CreateRunResponse toResponse(AiRecommendationRun run, AiRecommendationPolicy policy) {
        return new CreateRunResponse(
                run.id(),
                run.status().name(),
                run.projectId(),
                policy.code(),
                run.requestedPackCodes(),
                Map.of("self", "/api/ai-recommendations/runs/" + run.id()));
    }
}
