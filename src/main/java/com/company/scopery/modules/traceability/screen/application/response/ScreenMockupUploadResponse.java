package com.company.scopery.modules.traceability.screen.application.response;
import java.time.Instant;
import java.util.UUID;
public record ScreenMockupUploadResponse(UUID screenId, String uploadUrl, String objectKey, Instant expiresAt, long maxSizeBytes) {}
