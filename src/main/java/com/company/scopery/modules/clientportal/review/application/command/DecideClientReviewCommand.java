package com.company.scopery.modules.clientportal.review.application.command;
import java.util.UUID;
public record DecideClientReviewCommand(UUID projectId, UUID reviewId, String outcome, String comment) {}
