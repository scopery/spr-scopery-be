package com.company.scopery.modules.documenthub.comment.http.request;

import jakarta.validation.constraints.NotBlank;

public record OpenCommentThreadRequest(
        String blockId,
        String anchorText,
        @NotBlank String firstCommentBody
) {}
