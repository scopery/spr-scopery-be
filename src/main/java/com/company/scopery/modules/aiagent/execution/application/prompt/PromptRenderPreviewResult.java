package com.company.scopery.modules.aiagent.execution.application.prompt;

import java.util.List;

public record PromptRenderPreviewResult(String renderedText, List<String> missingVariables) {}
