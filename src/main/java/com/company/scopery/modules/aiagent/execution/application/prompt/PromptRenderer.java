package com.company.scopery.modules.aiagent.execution.application.prompt;

import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PromptRenderer {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    public PromptRenderPreviewResult renderPreview(String template, Map<String, String> variables) {
        if (template == null) {
            throw AiAgentExceptions.promptRenderFailed("template content is null");
        }
        Map<String, String> vars = variables != null ? variables : Map.of();
        List<String> missing = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER.matcher(template);
        while (matcher.find()) {
            String varName = matcher.group(1);
            String value = vars.get(varName);
            if (value == null) {
                if (!missing.contains(varName)) missing.add(varName);
                matcher.appendReplacement(result, Matcher.quoteReplacement("{{" + varName + "}}"));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(value));
            }
        }
        matcher.appendTail(result);
        return new PromptRenderPreviewResult(result.toString(), missing);
    }

    public String render(String template, Map<String, String> variables) {
        if (template == null) {
            throw AiAgentExceptions.promptRenderFailed("template content is null");
        }
        Map<String, String> vars = variables != null ? variables : Map.of();
        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER.matcher(template);
        while (matcher.find()) {
            String varName = matcher.group(1);
            String value = vars.get(varName);
            if (value == null) {
                throw AiAgentExceptions.promptRenderVariableMissing(varName);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
