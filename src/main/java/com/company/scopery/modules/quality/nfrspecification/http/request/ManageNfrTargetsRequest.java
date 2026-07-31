package com.company.scopery.modules.quality.nfrspecification.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List; import java.util.UUID;
public record ManageNfrTargetsRequest(List<TargetItemRequest> targets) {
    public record TargetItemRequest(
            @Schema(allowableValues = {"SYSTEM","MODULE","FUNCTION","API","COMPONENT","ENTITY","INFRASTRUCTURE"})
            String targetType,
            UUID targetId, String targetLabel, int displayOrder) {}
}
