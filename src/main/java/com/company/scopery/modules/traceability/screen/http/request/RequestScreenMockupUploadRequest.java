package com.company.scopery.modules.traceability.screen.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record RequestScreenMockupUploadRequest(
    @NotBlank
    @Pattern(regexp = "image/(jpeg|png|gif|webp|svg\\+xml)",
             message = "contentType must be image/jpeg, image/png, image/gif, image/webp, or image/svg+xml")
    String contentType
) {}
