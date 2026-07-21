package com.company.scopery.modules.aiaction.application.port;

import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;

import java.util.Map;

public interface AiActionToolAdapter {

    String toolCode();

    String toolVersion();

    AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step);

    AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution);

    AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution);
}
