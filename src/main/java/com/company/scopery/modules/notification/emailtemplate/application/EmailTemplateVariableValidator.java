package com.company.scopery.modules.notification.emailtemplate.application;

import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EmailTemplateVariableValidator {

    private final EmailTemplateRenderer renderer;

    public EmailTemplateVariableValidator(EmailTemplateRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Validates that every {{variable.path}} used in the template content
     * exists in the allowedPaths set from the Event Registry.
     * Throws AppException (422) on the first undeclared variable found.
     */
    public void validate(String subjectTemplate, String htmlBodyTemplate,
                         String textBodyTemplate, Set<String> allowedPaths) {
        validateContent(subjectTemplate, allowedPaths);
        validateContent(htmlBodyTemplate, allowedPaths);
        if (textBodyTemplate != null && !textBodyTemplate.isBlank()) {
            validateContent(textBodyTemplate, allowedPaths);
        }
    }

    private void validateContent(String content, Set<String> allowedPaths) {
        if (content == null) return;
        for (String path : renderer.extractVariablePaths(content)) {
            if (!allowedPaths.contains(path)) {
                throw NotificationExceptions.emailTemplateVersionVariableMissing(path);
            }
        }
    }
}
