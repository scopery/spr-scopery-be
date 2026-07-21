package com.company.scopery.modules.aiaction.application.service;

import com.company.scopery.modules.aiaction.application.response.AiActionConfirmationResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionPlanResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionPreviewResponse;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionConfirmation;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionConfirmationRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPreview;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPreviewRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AiActionPlanQueryService {

    private final AiActionPlanRepository planRepository;
    private final AiActionPreviewRepository previewRepository;
    private final AiActionConfirmationRepository confirmationRepository;

    public AiActionPlanQueryService(AiActionPlanRepository planRepository,
                                    AiActionPreviewRepository previewRepository,
                                    AiActionConfirmationRepository confirmationRepository) {
        this.planRepository = planRepository;
        this.previewRepository = previewRepository;
        this.confirmationRepository = confirmationRepository;
    }

    @Transactional(readOnly = true)
    public AiActionPlanResponse getPlan(UUID planId) {
        AiActionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> AiActionExceptions.planNotFound(planId));
        return toPlanResponse(plan);
    }

    @Transactional(readOnly = true)
    public AiActionPreviewResponse getPreview(UUID planId) {
        planRepository.findById(planId)
                .orElseThrow(() -> AiActionExceptions.planNotFound(planId));
        AiActionPreview preview = previewRepository.findByPlanId(planId)
                .orElseThrow(() -> AiActionExceptions.previewRequired(planId));
        return toPreviewResponse(preview);
    }

    @Transactional(readOnly = true)
    public AiActionConfirmationResponse getConfirmation(UUID planId) {
        planRepository.findById(planId)
                .orElseThrow(() -> AiActionExceptions.planNotFound(planId));
        AiActionConfirmation confirmation = confirmationRepository.findLatestByPlanId(planId)
                .orElseThrow(() -> AiActionExceptions.confirmationRequired(planId));
        return toConfirmationResponse(confirmation);
    }

    private AiActionPlanResponse toPlanResponse(AiActionPlan plan) {
        return new AiActionPlanResponse(
                plan.id(), plan.requestId(), plan.planNumber(),
                plan.status().name(), plan.planHash(), plan.version(),
                plan.summary(), plan.riskLevel() != null ? plan.riskLevel().name() : null,
                plan.executionMode() != null ? plan.executionMode().name() : null,
                plan.requiresConfirmation(), plan.stepCount(), plan.targetCount(),
                plan.expiresAt(), plan.createdAt());
    }

    private AiActionPreviewResponse toPreviewResponse(AiActionPreview preview) {
        return new AiActionPreviewResponse(
                preview.id(), preview.planId(), preview.previewHash(),
                preview.maskedDiffJson(), preview.warningsJson(),
                preview.baselineImpact(), preview.externalSideEffect(),
                preview.validUntil(), preview.createdAt());
    }

    private AiActionConfirmationResponse toConfirmationResponse(AiActionConfirmation confirmation) {
        return new AiActionConfirmationResponse(
                confirmation.id(), confirmation.planId(), confirmation.planHash(),
                confirmation.decision().name(), confirmation.status().name(),
                confirmation.expiresAt(), confirmation.createdAt());
    }
}
