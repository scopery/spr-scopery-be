package com.company.scopery.modules.aiagent.playground.application.action;

import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.execution.application.action.ExecuteEventConfigAction;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventConfigCommand;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionRunResponse;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.playground.domain.enums.PlaygroundMode;
import com.company.scopery.modules.aiagent.playground.application.command.RunPlaygroundEventConfigCommand;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundRunResponse;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RunEventConfigAction {

    private final ExecuteEventConfigAction executeEventConfigAction;
    private final EventConfigRepository eventConfigRepository;
    private final boolean playgroundEnabled;
    private final String runtimeEnvironment;

    public RunEventConfigAction(ExecuteEventConfigAction executeEventConfigAction,
                                 EventConfigRepository eventConfigRepository,
                                 @Value("${scopery.aiagent.playground.enabled:true}") boolean playgroundEnabled,
                                 @Value("${scopery.aiagent.runtime-environment:DEV}") String runtimeEnvironment) {
        this.executeEventConfigAction = executeEventConfigAction;
        this.eventConfigRepository = eventConfigRepository;
        this.playgroundEnabled = playgroundEnabled;
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public PlaygroundRunResponse execute(RunPlaygroundEventConfigCommand command) {
        if (!playgroundEnabled || "PROD".equalsIgnoreCase(runtimeEnvironment)) {
            throw AiAgentExceptions.playgroundDisabledInEnvironment(runtimeEnvironment);
        }

        var eventConfig = eventConfigRepository.findById(command.eventConfigId())
                .orElseThrow(() -> AiAgentExceptions.playgroundEventConfigNotFound(command.eventConfigId()));
        if (eventConfig.status() != EventConfigStatus.ACTIVE) {
            throw AiAgentExceptions.playgroundEventConfigNotActive(command.eventConfigId());
        }

        ExecuteEventConfigCommand execCommand = new ExecuteEventConfigCommand(
                command.requestId(),
                command.eventConfigId(),
                ExecutionTriggerSource.PLAYGROUND.name(),
                command.inputVariables());

        ExecutionRunResponse result = executeEventConfigAction.execute(execCommand);
        return PlaygroundRunResponse.from(result, PlaygroundMode.EVENT_CONFIG);
    }
}
