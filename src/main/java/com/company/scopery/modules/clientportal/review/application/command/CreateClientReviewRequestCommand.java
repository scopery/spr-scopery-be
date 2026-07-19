package com.company.scopery.modules.clientportal.review.application.command;
import java.util.UUID;
public record CreateClientReviewRequestCommand(UUID projectId, String targetType, UUID targetId, String title, UUID assignedPortalAccountId){}
