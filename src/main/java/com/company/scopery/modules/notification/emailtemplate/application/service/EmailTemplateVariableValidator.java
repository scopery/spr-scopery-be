package com.company.scopery.modules.notification.emailtemplate.application.service;

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
        validate(subjectTemplate, htmlBodyTemplate, textBodyTemplate, allowedPaths, Set.of(), false);
    }

    /**
     * @param sensitivePaths       paths marked sensitive in Event Registry
     * @param allowSensitiveInBody when false, reject sensitive variables in body;
     *                             sensitive variables are always rejected in subject
     */
    public void validate(String subjectTemplate, String htmlBodyTemplate,
                         String textBodyTemplate, Set<String> allowedPaths,
                         Set<String> sensitivePaths, boolean allowSensitiveInBody) {
        Set<String> sensitive = sensitivePaths == null ? Set.of() : sensitivePaths;
        validateContent(subjectTemplate, allowedPaths);
        rejectSensitiveInSubject(subjectTemplate, sensitive);
        validateContent(htmlBodyTemplate, allowedPaths);
        rejectSensitiveInBody(htmlBodyTemplate, sensitive, allowSensitiveInBody);
        if (textBodyTemplate != null && !textBodyTemplate.isBlank()) {
            validateContent(textBodyTemplate, allowedPaths);
            rejectSensitiveInBody(textBodyTemplate, sensitive, allowSensitiveInBody);
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

    private void rejectSensitiveInSubject(String subject, Set<String> sensitivePaths) {
        if (subject == null || sensitivePaths.isEmpty()) return;
        for (String path : renderer.extractVariablePaths(subject)) {
            if (sensitivePaths.contains(path)) {
                throw NotificationExceptions.emailTemplateSensitiveVariableNotAllowed(path, "subject");
            }
        }
    }

    private void rejectSensitiveInBody(String body, Set<String> sensitivePaths, boolean allowSensitiveInBody) {
        if (body == null || sensitivePaths.isEmpty() || allowSensitiveInBody) return;
        for (String path : renderer.extractVariablePaths(body)) {
            if (sensitivePaths.contains(path)) {
                throw NotificationExceptions.emailTemplateSensitiveVariableNotAllowed(path, "body");
            }
        }
    }
}
