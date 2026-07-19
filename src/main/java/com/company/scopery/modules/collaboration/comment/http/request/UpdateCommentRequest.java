package com.company.scopery.modules.collaboration.comment.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateCommentRequest(@NotBlank String body) {}
