package com.company.scopery.modules.scope.review.application.command;
import java.util.UUID;
public record ApproveReviewCommand(UUID projectId, UUID reviewId, String decision) {}
