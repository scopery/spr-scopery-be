package com.company.scopery.modules.traceability.requirement.http.request;
import java.util.UUID;
public record UpdateRequirementRequest(String title, String description, String priority, String requirementType, UUID applicationId, UUID functionalItemId, UUID nonFunctionalItemId){}
