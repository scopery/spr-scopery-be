package com.company.scopery.modules.traceability.appcomponent.application.response;
import java.time.Instant;
import java.util.UUID;
public record ComponentScreenshotUploadResponse(UUID componentId, String uploadUrl, String objectKey, Instant expiresAt, long maxSizeBytes) {}
