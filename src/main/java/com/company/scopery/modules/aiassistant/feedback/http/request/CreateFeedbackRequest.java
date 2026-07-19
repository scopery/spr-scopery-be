package com.company.scopery.modules.aiassistant.feedback.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateFeedbackRequest(
        @NotNull UUID conversationId,
        @NotNull UUID messageId,
        @NotBlank String rating,
        String reasonCode,
        @Size(max = 2000) String comment
) {}
