package com.company.scopery.modules.aiassistant.shared.listeners;

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
@Order(110)
public class AiAssistantPermissionInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantPermissionInitializer.class);
    private static final String MODULE = "AIASSISTANT";

    private final IamRightRepository rightRepository;

    public AiAssistantPermissionInitializer(IamRightRepository rightRepository) {
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
            log.info("[AiAssistantPermissionSeed] Seeded {} new AI assistant IAM rights", seeded);
        }
    }

    private record RightDef(String code, String name, String description) {}

    private static final List<RightDef> RIGHTS = List.of(
            new RightDef("AI_ASSISTANT_USE",
                    "AI Assistant Use",
                    "Access and use the contextual AI assistant"),
            new RightDef("AI_ASSISTANT_PROJECT_USE",
                    "AI Assistant Project Use",
                    "Use the AI assistant in a project context for grounded answers"),
            new RightDef("AI_ASSISTANT_CONVERSATION_VIEW",
                    "AI Assistant Conversation View",
                    "View AI assistant conversation history"),
            new RightDef("AI_ASSISTANT_CONVERSATION_MANAGE",
                    "AI Assistant Conversation Manage",
                    "Archive and delete AI assistant conversations"),
            new RightDef("AI_ASSISTANT_GUIDE_USE",
                    "AI Assistant Guide Use",
                    "Access page, field, and action explanation guides"),
            new RightDef("AI_ASSISTANT_TRACEABILITY_USE",
                    "AI Assistant Traceability Use",
                    "Ask traceability and requirement linkage questions"),
            new RightDef("AI_ASSISTANT_FEEDBACK_CREATE",
                    "AI Assistant Feedback Create",
                    "Submit thumbs-up/down feedback on AI assistant responses"),
            new RightDef("AI_ASSISTANT_ADMIN_VIEW",
                    "AI Assistant Admin View",
                    "View AI assistant usage statistics and quota reports"),
            new RightDef("AI_ASSISTANT_PROMPT_MANAGE",
                    "AI Assistant Prompt Manage",
                    "Manage AI assistant prompt profiles and configurations")
    );
}
