package com.company.scopery.modules.externalparty.relationship.application.command;
import java.util.UUID;
public record CreateProjectExternalPartyRelationshipCommand(UUID projectId, UUID organizationId, String relationshipType, String notes) {}
