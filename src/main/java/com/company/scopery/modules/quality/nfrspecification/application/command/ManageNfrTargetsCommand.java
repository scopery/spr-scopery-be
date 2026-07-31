package com.company.scopery.modules.quality.nfrspecification.application.command;
import java.util.List; import java.util.UUID;
public record ManageNfrTargetsCommand(UUID projectId, UUID requirementId, List<TargetItem> targets) {
    public record TargetItem(String targetType, UUID targetId, String targetLabel, int displayOrder) {}
}
