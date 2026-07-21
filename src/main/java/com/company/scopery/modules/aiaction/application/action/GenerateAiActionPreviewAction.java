package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.modules.aiaction.application.port.AiActionSensitiveFieldMaskingPort;
import com.company.scopery.modules.aiaction.application.response.AiActionPreviewResponse;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPreview;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPreviewRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class GenerateAiActionPreviewAction {

    private final AiActionPlanRepository planRepository;
    private final AiActionPreviewRepository previewRepository;
    private final AiActionSensitiveFieldMaskingPort maskingPort;

    public GenerateAiActionPreviewAction(AiActionPlanRepository planRepository,
                                         AiActionPreviewRepository previewRepository,
                                         AiActionSensitiveFieldMaskingPort maskingPort) {
        this.planRepository = planRepository;
        this.previewRepository = previewRepository;
        this.maskingPort = maskingPort;
    }

    @Transactional
    public AiActionPreviewResponse execute(UUID planId) {
        AiActionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> AiActionExceptions.planNotFound(planId));

        String maskedDiff = maskingPort.maskDiff("PLAN", planId, Map.of("planId", planId.toString()));

        AiActionPreview preview = AiActionPreview.create(
                plan.id(), plan.planHash(), maskedDiff, null, null, false,
                Instant.now().plusSeconds(900));

        AiActionPreview saved = previewRepository.save(preview);

        return new AiActionPreviewResponse(
                saved.id(), saved.planId(), saved.previewHash(),
                saved.maskedDiffJson(), saved.warningsJson(),
                saved.baselineImpact(), saved.externalSideEffect(),
                saved.validUntil(), saved.createdAt());
    }
}
