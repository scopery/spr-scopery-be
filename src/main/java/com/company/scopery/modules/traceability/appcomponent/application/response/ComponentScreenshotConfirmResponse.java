package com.company.scopery.modules.traceability.appcomponent.application.response;
import java.util.UUID;
public record ComponentScreenshotConfirmResponse(UUID componentId, String screenshotObjectKey, String screenshotUrl) {}
