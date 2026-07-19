package com.company.scopery.modules.governance.versioning.application.response;
import java.util.UUID;
public record SnapshotDiffResponse(UUID leftSnapshotId, UUID rightSnapshotId, boolean identical, String leftJson, String rightJson) {}
