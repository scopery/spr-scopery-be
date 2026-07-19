package com.company.scopery.modules.documenthub.attachment.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAttachmentRequest(
        @NotBlank String fileName,
        String mediaType,
        Long fileSizeBytes,
        String blockId
) {}
