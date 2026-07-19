package com.company.scopery.modules.clientportal.feedback.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateClientFeedbackRequest(@NotBlank String category, @NotBlank String title, String body) {}
