package com.company.scopery.modules.aiagent.tool.application.action;

import com.company.scopery.modules.aiagent.tool.application.command.BindAgentToolCommand;
import com.company.scopery.modules.aiagent.tool.application.response.AiAgentToolBindingResponse;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiAgentToolBindingStatus;
import com.company.scopery.modules.aiagent.tool.domain.model.AiAgentToolBinding;
import com.company.scopery.modules.aiagent.tool.domain.model.AiAgentToolBindingRepository;
import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolRepository;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BindAgentToolAction {

    private final AiToolRepository toolRepository;
    private final AgentRepository agentRepository;
    private final AiAgentToolBindingRepository bindingRepository;
    private final AiAgentActivityLogger activityLogger;

    public BindAgentToolAction(AiToolRepository toolRepository,
                               AgentRepository agentRepository,
                               AiAgentToolBindingRepository bindingRepository,
                               AiAgentActivityLogger activityLogger) {
        this.toolRepository = toolRepository;
        this.agentRepository = agentRepository;
        this.bindingRepository = bindingRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiAgentToolBindingResponse execute(BindAgentToolCommand command) {
        AiTool tool = toolRepository.findById(command.toolId())
                .orElseThrow(() -> AiAgentExceptions.aiToolNotFound(command.toolId()));
        Agent agent = agentRepository.findById(command.agentId())
                .orElseThrow(() -> AiAgentExceptions.agentNotFound(command.agentId()));
        if (agent.status() != AgentStatus.ACTIVE) {
            throw AiAgentExceptions.aiToolAgentNotActive(agent.code().value());
        }

        AiAgentToolBinding binding = bindingRepository.findByAgentIdAndToolId(agent.id(), tool.id())
                .orElse(null);
        if (binding == null) {
            binding = AiAgentToolBinding.create(agent.id(), tool.id());
        } else if (binding.status() == AiAgentToolBindingStatus.INACTIVE) {
            binding.activate();
        } else {
            throw AiAgentExceptions.aiToolBindingAlreadyExists(agent.code().value(), tool.code().value());
        }
        AiAgentToolBinding saved = bindingRepository.save(binding);
        activityLogger.logSuccess(AiAgentEntityTypes.AI_AGENT_TOOL_BINDING, saved.id(),
                AiAgentActivityActions.BIND_AI_TOOL,
                "Bound tool " + tool.code().value() + " to agent " + agent.code().value());
        return AiAgentToolBindingResponse.from(saved);
    }
}
