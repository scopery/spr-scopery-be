package com.company.scopery.modules.scope.review.application.command;
import java.util.UUID;
public record SubmitDeliverableReviewCommand(UUID projectId, UUID deliverableId) {}
