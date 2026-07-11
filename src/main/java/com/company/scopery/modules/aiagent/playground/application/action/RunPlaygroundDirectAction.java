package com.company.scopery.modules.aiagent.playground.application.action;

import com.company.scopery.modules.aiagent.execution.application.action.ExecutePlaygroundDirectAction;
import com.company.scopery.modules.aiagent.execution.application.command.ExecutePlaygroundDirectCommand;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionRunResponse;
import com.company.scopery.modules.aiagent.playground.domain.enums.PlaygroundMode;
import com.company.scopery.modules.aiagent.playground.application.command.RunPlaygroundDirectCommand;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundRunResponse;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RunPlaygroundDirectAction {

    private final ExecutePlaygroundDirectAction executePlaygroundDirectAction;
    private final boolean playgroundEnabled;
    private final String runtimeEnvironment;

    public RunPlaygroundDirectAction(ExecutePlaygroundDirectAction executePlaygroundDirectAction,
                                      @Value("${scopery.aiagent.playground.enabled:true}") boolean playgroundEnabled,
                                      @Value("${scopery.aiagent.runtime-environment:DEV}") String runtimeEnvironment) {
        this.executePlaygroundDirectAction = executePlaygroundDirectAction;
        this.playgroundEnabled = playgroundEnabled;
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public PlaygroundRunResponse execute(RunPlaygroundDirectCommand command) {
        if (!playgroundEnabled || "PROD".equalsIgnoreCase(runtimeEnvironment)) {
            throw AiAgentExceptions.playgroundDisabledInEnvironment(runtimeEnvironment);
        }

        ExecutePlaygroundDirectCommand execCommand = new ExecutePlaygroundDirectCommand(
                command.requestId(),
                command.agentId(),
                command.promptVersionId(),
                command.modelDeploymentId(),
                command.inputVariables());

        ExecutionRunResponse result = executePlaygroundDirectAction.execute(execCommand);
        return PlaygroundRunResponse.from(result, PlaygroundMode.DIRECT);
    }
}
