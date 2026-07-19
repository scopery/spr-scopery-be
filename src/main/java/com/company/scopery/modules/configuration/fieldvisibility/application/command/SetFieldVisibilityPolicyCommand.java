package com.company.scopery.modules.configuration.fieldvisibility.application.command;
import java.util.UUID;
public record SetFieldVisibilityPolicyCommand(UUID workspaceId, UUID fieldId, String audienceType, Boolean visible) {}
