package com.company.scopery.modules.documenthub.comment.http.request;

import jakarta.validation.constraints.NotBlank;

public record AddCommentRequest(@NotBlank String body) {}
