package com.company.scopery.modules.aiaction.application.gateway;

import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolRegistryPort;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AiActionToolGateway {

    private final AiActionToolRegistryPort toolRegistryPort;

    public AiActionToolGateway(AiActionToolRegistryPort toolRegistryPort) {
        this.toolRegistryPort = toolRegistryPort;
    }

    public AiActionToolResult execute(AiActionStep step, AiActionExecution execution) {
        AiActionToolPolicy policy = toolRegistryPort.requirePolicy(step.toolCode(), step.toolVersion());

        if (policy.executionMode() == AiActionExecutionMode.FORBIDDEN) {
            throw AiActionExceptions.forbidden("Tool " + step.toolCode() + " is marked FORBIDDEN");
        }

        AiActionToolAdapter adapter = toolRegistryPort.requireAdapter(step.toolCode(), step.toolVersion());

        Map<String, Object> input = Map.of(
                "targetEntityType", String.valueOf(step.targetEntityType()),
                "targetEntityId", String.valueOf(step.targetEntityId()),
                "inputHash", String.valueOf(step.inputHash()));

        return adapter.execute(input, step, execution);
    }

    public AiActionDryRunResult dryRun(AiActionStep step) {
        AiActionToolAdapter adapter = toolRegistryPort.requireAdapter(step.toolCode(), step.toolVersion());
        Map<String, Object> input = Map.of(
                "targetEntityType", String.valueOf(step.targetEntityType()),
                "targetEntityId", String.valueOf(step.targetEntityId()));
        return adapter.dryRun(input, step);
    }
}
