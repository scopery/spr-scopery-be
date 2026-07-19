package com.company.scopery.modules.scope.review.application.command;
import java.util.UUID;
public record RejectReviewCommand(UUID projectId, UUID reviewId, String reason) {}
