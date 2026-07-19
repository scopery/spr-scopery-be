package com.company.scopery.modules.airecommendation.application.listeners;

import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(50)
public class AiRecommendationPermissionInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiRecommendationPermissionInitializer.class);
    private static final String MODULE = "AIRECOMMENDATION";

    private final IamRightRepository rightRepository;

    public AiRecommendationPermissionInitializer(IamRightRepository rightRepository) {
        this.rightRepository = rightRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        int seeded = 0;
        for (RightDef def : RIGHTS) {
            IamRightCode code = IamRightCode.of(def.code());
            if (!rightRepository.existsByCode(code)) {
                rightRepository.save(IamRight.create(code, def.name(), def.description(), MODULE));
                seeded++;
            }
        }
        if (seeded > 0) {
            log.info("[AiRecommendationPermissionSeed] Seeded {} new AI recommendation IAM rights", seeded);
        }
    }

    private record RightDef(String code, String name, String description) {}

    private static final List<RightDef> RIGHTS = List.of(
            new RightDef("AI_RECOMMENDATION_VIEW",
                    "AI Recommendation View",
                    "View AI suggestion recommendations"),
            new RightDef("AI_RECOMMENDATION_GENERATE",
                    "AI Recommendation Generate",
                    "Trigger a recommendation generation run"),
            new RightDef("AI_RECOMMENDATION_EDIT",
                    "AI Recommendation Edit",
                    "Edit proposed items in a suggestion"),
            new RightDef("AI_RECOMMENDATION_ACCEPT",
                    "AI Recommendation Accept",
                    "Accept a suggestion (no domain mutation in Phase 43)"),
            new RightDef("AI_RECOMMENDATION_REJECT",
                    "AI Recommendation Reject",
                    "Reject a suggestion"),
            new RightDef("AI_RECOMMENDATION_SUPPRESS",
                    "AI Recommendation Suppress",
                    "Suppress a suggestion for a duration"),
            new RightDef("AI_RECOMMENDATION_FEEDBACK_CREATE",
                    "AI Recommendation Feedback Create",
                    "Submit helpfulness feedback on a suggestion"),
            new RightDef("AI_RECOMMENDATION_POLICY_VIEW",
                    "AI Recommendation Policy View",
                    "View recommendation policies and registry definitions"),
            new RightDef("AI_RECOMMENDATION_POLICY_MANAGE",
                    "AI Recommendation Policy Manage",
                    "Create and update recommendation policies"),
            new RightDef("AI_RECOMMENDATION_ANALYTICS_VIEW",
                    "AI Recommendation Analytics View",
                    "View recommendation run statistics and analytics")
    );
}
