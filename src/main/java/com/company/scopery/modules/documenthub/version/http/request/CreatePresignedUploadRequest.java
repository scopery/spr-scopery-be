package com.company.scopery.modules.documenthub.version.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePresignedUploadRequest(
        @NotBlank @Size(max = 500) String fileName,
        @NotBlank @Size(max = 200) String contentType,
        @Size(max = 1000) String changeNotes
) {}
