package com.company.scopery.modules.notification.emailtemplate.application.service;

import java.util.Map;
import java.util.Set;

public interface EmailTemplateRenderer {

    String render(String templateStr, Map<String, Object> payload);

    Set<String> extractVariablePaths(String templateStr);
}
