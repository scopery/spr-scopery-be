package com.company.scopery.modules.projectbaseline.changerequestitem.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
public record CreateChangeRequestItemRequest(
        @NotBlank String targetType, UUID targetId, @NotBlank String operation, @NotBlank String summary,
        String beforeSnapshotJson, String afterSnapshotJson, String applyPayloadJson,
        List<String> affectedAreas) {}
