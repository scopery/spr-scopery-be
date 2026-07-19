package com.company.scopery.modules.scope.review.application.command;
import java.util.UUID;
public record RequestReworkCommand(UUID projectId, UUID reviewId, String reason) {}
