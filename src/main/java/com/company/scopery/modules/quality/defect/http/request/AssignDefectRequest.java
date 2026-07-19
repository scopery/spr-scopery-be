package com.company.scopery.modules.quality.defect.http.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record AssignDefectRequest(@NotNull UUID assignedToUserId) {}
