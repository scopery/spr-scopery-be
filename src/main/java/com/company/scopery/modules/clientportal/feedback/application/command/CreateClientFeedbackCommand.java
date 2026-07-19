package com.company.scopery.modules.clientportal.feedback.application.command;
import java.util.UUID;
public record CreateClientFeedbackCommand(UUID projectId, String category, String title, String body, boolean portalCaller) {}
