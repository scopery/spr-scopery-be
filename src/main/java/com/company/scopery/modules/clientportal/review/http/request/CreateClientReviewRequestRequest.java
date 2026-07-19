package com.company.scopery.modules.clientportal.review.http.request;
import jakarta.validation.constraints.*; import java.util.UUID;
public record CreateClientReviewRequestRequest(@NotBlank String targetType, @NotNull UUID targetId, @NotBlank String title, UUID assignedPortalAccountId){}
