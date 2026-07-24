package com.company.scopery.modules.aiaction.application.port;

import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;

import java.util.Map;

public interface AiActionToolAdapter {

    String toolCode();

    String toolVersion();

    /** English description shown to the LLM when declaring this tool. */
    default String description() {
        return "Performs the " + toolCode() + " action.";
    }

    /** JSON Schema (draft-07) string for the tool's input parameters, used in LLM tool declarations. */
    default String parametersSchemaJson() {
        return "{\"type\":\"object\",\"properties\":{},\"required\":[]}";
    }

    /**
     * Resolves human-readable display hints from raw input args for the confirmation UI.
     * Override to provide tool-specific resolved values (e.g. phase name from phaseId).
     */
    default Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        return Map.of();
    }

    AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step);

    AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution);

    AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution);
}
