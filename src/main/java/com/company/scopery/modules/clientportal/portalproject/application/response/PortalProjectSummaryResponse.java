package com.company.scopery.modules.clientportal.portalproject.application.response;
import java.util.UUID;
public record PortalProjectSummaryResponse(UUID projectId, UUID workspaceId, String permissionPolicyCode, String grantStatus) {}
