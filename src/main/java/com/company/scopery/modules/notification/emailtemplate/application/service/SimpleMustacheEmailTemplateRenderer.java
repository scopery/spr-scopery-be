package com.company.scopery.modules.notification.emailtemplate.application.service;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves {{variable.path}} placeholders via map lookup only.
 * No method execution, no scripting, no DB access.
 */
@Component
public class SimpleMustacheEmailTemplateRenderer implements EmailTemplateRenderer {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^{}]+?)\\}\\}");

    @Override
    public String render(String templateStr, Map<String, Object> payload) {
        if (templateStr == null) return null;
        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(templateStr);
        while (matcher.find()) {
            String path = matcher.group(1).trim();
            Object value = resolvePath(path, payload);
            String replacement = value != null ? Matcher.quoteReplacement(String.valueOf(value)) : "";
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    @Override
    public Set<String> extractVariablePaths(String templateStr) {
        Set<String> paths = new LinkedHashSet<>();
        if (templateStr == null) return paths;
        Matcher matcher = VARIABLE_PATTERN.matcher(templateStr);
        while (matcher.find()) {
            paths.add(matcher.group(1).trim());
        }
        return paths;
    }

    @SuppressWarnings("unchecked")
    private Object resolvePath(String path, Map<String, Object> payload) {
        if (payload == null) return null;
        String[] parts = path.split("\\.", 2);
        Object val = payload.get(parts[0]);
        if (parts.length == 1 || val == null) return val;
        if (val instanceof Map<?, ?> nested) {
            return resolvePath(parts[1], (Map<String, Object>) nested);
        }
        return null;
    }
}
