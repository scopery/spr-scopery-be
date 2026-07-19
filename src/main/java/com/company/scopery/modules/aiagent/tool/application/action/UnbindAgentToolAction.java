package com.company.scopery.modules.aiagent.tool.application.action;

import com.company.scopery.modules.aiagent.tool.application.command.UnbindAgentToolCommand;
import com.company.scopery.modules.aiagent.tool.application.response.AiAgentToolBindingResponse;
import com.company.scopery.modules.aiagent.tool.domain.model.AiAgentToolBinding;
import com.company.scopery.modules.aiagent.tool.domain.model.AiAgentToolBindingRepository;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UnbindAgentToolAction {

    private final AiToolRepository toolRepository;
    private final AiAgentToolBindingRepository bindingRepository;
    private final AiAgentActivityLogger activityLogger;

    public UnbindAgentToolAction(AiToolRepository toolRepository,
                                 AiAgentToolBindingRepository bindingRepository,
                                 AiAgentActivityLogger activityLogger) {
        this.toolRepository = toolRepository;
        this.bindingRepository = bindingRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiAgentToolBindingResponse execute(UnbindAgentToolCommand command) {
        toolRepository.findById(command.toolId())
                .orElseThrow(() -> AiAgentExceptions.aiToolNotFound(command.toolId()));
        AiAgentToolBinding binding = bindingRepository.findByAgentIdAndToolId(command.agentId(), command.toolId())
                .orElseThrow(() -> AiAgentExceptions.aiToolBindingNotFound(command.agentId(), command.toolId()));
        binding.deactivate();
        AiAgentToolBinding saved = bindingRepository.save(binding);
        activityLogger.logSuccess(AiAgentEntityTypes.AI_AGENT_TOOL_BINDING, saved.id(),
                AiAgentActivityActions.UNBIND_AI_TOOL,
                "Unbound agent " + command.agentId() + " from tool " + command.toolId());
        return AiAgentToolBindingResponse.from(saved);
    }
}
