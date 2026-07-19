package com.company.scopery.modules.clientportal.comment.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateClientCommentRequest(@NotBlank String targetType, @NotNull UUID targetId, @NotBlank String body) {}
